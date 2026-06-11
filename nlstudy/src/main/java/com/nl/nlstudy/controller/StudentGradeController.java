package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.*;
import com.nl.nlstudy.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "学生端-成绩报告")
@RestController
@RequestMapping("/student/grades")
@RequiredArgsConstructor
public class StudentGradeController {

    private final GradeMapper gradeMapper;
    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final GradeReviewMapper gradeReviewMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final QuestionMapper questionMapper;

    @Operation(summary = "获取成绩列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) Long courseId,
            HttpServletRequest request) {
        Long studentId = getCurrentUserId(request);
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }

        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getStudentId, studentId);
        if (courseId != null) {
            // 需要通过examId关联courseId来过滤
            wrapper.inSql(Grade::getExamId, 
                "SELECT id FROM exam WHERE course_id = " + courseId);
        }
        wrapper.orderByDesc(Grade::getPublishTime);
        List<Grade> grades = gradeMapper.selectList(wrapper);

        List<Map<String, Object>> result = grades.stream().map(g -> {
            Map<String, Object> map = new HashMap<>();
            map.put("gradeId", g.getId());
            map.put("examId", g.getExamId());
            map.put("totalScore", g.getTotalScore());
            map.put("objectiveScore", g.getObjectiveScore());
            map.put("subjectiveScore", g.getSubjectiveScore());
            map.put("rank", g.getRank());
            map.put("percentile", g.getPercentile());
            map.put("status", g.getStatus());
            map.put("publishTime", g.getPublishTime());

            // 获取考试信息
            Exam exam = examMapper.selectById(g.getExamId());
            if (exam != null) {
                map.put("examName", exam.getName());
                map.put("courseId", exam.getCourseId());
                map.put("fullScore", exam.getTotalScore());
            }
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "获取成绩详情")
    @GetMapping("/{gradeId}")
    public Result<Map<String, Object>> detail(@PathVariable Long gradeId, HttpServletRequest request) {
        Grade grade = gradeMapper.selectById(gradeId);
        if (grade == null) {
            return Result.error("成绩不存在");
        }
        Long studentId = getCurrentUserId(request);
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }
        if (!studentId.equals(grade.getStudentId())) {
            return Result.error("无权查看该成绩");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("gradeId", grade.getId());
        result.put("examId", grade.getExamId());
        result.put("totalScore", grade.getTotalScore());
        result.put("objectiveScore", grade.getObjectiveScore());
        result.put("subjectiveScore", grade.getSubjectiveScore());
        result.put("rank", grade.getRank());
        result.put("percentile", grade.getPercentile());
        result.put("status", grade.getStatus());
        result.put("publishTime", grade.getPublishTime());

        LambdaQueryWrapper<GradeReview> reviewWrapper = new LambdaQueryWrapper<>();
        reviewWrapper.eq(GradeReview::getGradeId, gradeId)
                .eq(GradeReview::getStudentId, grade.getStudentId())
                .orderByDesc(GradeReview::getCreateTime);
        List<GradeReview> reviews = gradeReviewMapper.selectList(reviewWrapper);
        List<Map<String, Object>> reviewItems = reviews.stream().map(review -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", review.getId());
            item.put("gradeId", review.getGradeId());
            item.put("examId", review.getExamId());
            item.put("questionId", review.getQuestionId());
            item.put("reason", review.getReason());
            item.put("studentComment", review.getStudentComment());
            item.put("originalScore", review.getOriginalScore());
            item.put("newScore", review.getNewScore());
            item.put("teacherComment", review.getTeacherComment());
            item.put("status", review.getStatus());
            item.put("createTime", review.getCreateTime());
            item.put("handleTime", review.getHandleTime());
            return item;
        }).collect(Collectors.toList());
        result.put("reviews", reviewItems);
        GradeReview wholeReview = reviews.stream()
                .filter(review -> review.getQuestionId() == null)
                .findFirst()
                .orElse(null);
        result.put("reviewStatus", wholeReview != null ? wholeReview.getStatus() : null);

        Exam exam = examMapper.selectById(grade.getExamId());
        if (exam != null) {
            result.put("examName", exam.getName());
            result.put("fullScore", exam.getTotalScore());
            result.put("courseId", exam.getCourseId());
        }

        LambdaQueryWrapper<Grade> rankWrapper = new LambdaQueryWrapper<>();
        rankWrapper.eq(Grade::getExamId, grade.getExamId())
                .isNotNull(Grade::getTotalScore)
                .orderByDesc(Grade::getTotalScore);
        List<Grade> examGrades = gradeMapper.selectList(rankWrapper);
        int totalRankStudents = examGrades.size();
        int computedRank = 0;
        for (int i = 0; i < examGrades.size(); i++) {
            if (grade.getId().equals(examGrades.get(i).getId())) {
                computedRank = i + 1;
                break;
            }
        }
        if (computedRank > 0) {
            result.put("rank", computedRank);
            double percentile = totalRankStudents > 0 ? Math.round((1 - ((computedRank - 1) * 1.0 / totalRankStudents)) * 1000) / 10.0 : 0.0;
            result.put("percentile", percentile);
        } else {
            result.put("rank", null);
            result.put("percentile", null);
        }

        LambdaQueryWrapper<Grade> statsWrapper = new LambdaQueryWrapper<>();
        statsWrapper.eq(Grade::getExamId, grade.getExamId()).isNotNull(Grade::getTotalScore);
        List<Grade> allGrades = gradeMapper.selectList(statsWrapper);
        double avgScore = allGrades.stream().mapToDouble(g -> g.getTotalScore() != null ? g.getTotalScore().doubleValue() : 0.0).average().orElse(0.0);
        double maxScore = allGrades.stream().mapToDouble(g -> g.getTotalScore() != null ? g.getTotalScore().doubleValue() : 0.0).max().orElse(0.0);
        result.put("classAvgScore", Math.round(avgScore * 100) / 100.0);
        result.put("maxScore", maxScore);
        result.put("totalStudents", allGrades.size());

        ExamRecord examRecord = null;
        if (grade.getExamRecordId() != null) {
            examRecord = examRecordMapper.selectById(grade.getExamRecordId());
        }

        List<Map<String, Object>> questions = new java.util.ArrayList<>();
        int correctCount = 0;
        if (examRecord != null) {
            LambdaQueryWrapper<AnswerRecord> answerWrapper = new LambdaQueryWrapper<>();
            answerWrapper.eq(AnswerRecord::getExamRecordId, examRecord.getId());
            List<AnswerRecord> answers = answerRecordMapper.selectList(answerWrapper);

            for (AnswerRecord answer : answers) {
                Question question = questionMapper.selectById(answer.getQuestionId());
                if (question == null) continue;
                boolean isCorrect = Boolean.TRUE.equals(answer.getIsCorrect());
                if (isCorrect) correctCount++;
                Map<String, Object> q = new HashMap<>();
                q.put("id", question.getId());
                q.put("type", question.getType());
                q.put("content", question.getContent());
                q.put("score", question.getScore());
                q.put("myScore", answer.getScore());
                q.put("studentAnswer", answer.getStudentAnswer());
                q.put("correctAnswer", question.getAnswer());
                q.put("knowledgePoints", question.getKnowledgePoints());
                q.put("isCorrect", isCorrect);
                GradeReview questionReview = reviews.stream()
                        .filter(review -> review.getQuestionId() != null && review.getQuestionId().equals(question.getId()))
                        .findFirst()
                        .orElse(null);
                q.put("reviewStatus", questionReview != null ? questionReview.getStatus() : null);
                q.put("reviewId", questionReview != null ? questionReview.getId() : null);
                questions.add(q);
            }
        }

        result.put("questions", questions);
        result.put("correctCount", correctCount);
        result.put("totalQuestions", questions.size());

        java.util.Map<String, double[]> kp = new java.util.LinkedHashMap<>();
        for (Map<String, Object> q : questions) {
            String knowledge = String.valueOf(q.getOrDefault("knowledgePoints", "")).trim();
            if (!knowledge.isBlank() && !"null".equalsIgnoreCase(knowledge)) {
                double questionScore = toDouble(q.get("score"));
                double myScore = toDouble(q.get("myScore"));
                if (questionScore <= 0) {
                    questionScore = 1.0;
                    myScore = Boolean.TRUE.equals(q.get("isCorrect")) ? 1.0 : 0.0;
                }
                double[] stat = kp.computeIfAbsent(knowledge, key -> new double[]{0.0, 0.0});
                stat[0] += myScore;
                stat[1] += questionScore;
            }
        }
        java.util.List<Map<String, Object>> knowledgePoints = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, double[]> e : kp.entrySet()) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("name", e.getKey());
            double[] stat = e.getValue();
            double value = stat[1] > 0 ? Math.round((stat[0] / stat[1]) * 1000) / 10.0 : 0.0;
            item.put("value", value);
            knowledgePoints.add(item);
        }
        result.put("knowledgePoints", knowledgePoints);

        java.util.List<Grade> courseGrades = new java.util.ArrayList<>();
        if (exam != null && exam.getCourseId() != null) {
            LambdaQueryWrapper<Grade> trendWrapper = new LambdaQueryWrapper<>();
            trendWrapper.eq(Grade::getStudentId, grade.getStudentId())
                    .isNotNull(Grade::getTotalScore)
                    .inSql(Grade::getExamId, "SELECT id FROM exam WHERE course_id = " + exam.getCourseId());
            courseGrades = gradeMapper.selectList(trendWrapper);
        }
        java.util.List<Grade> sortedGrades = new java.util.ArrayList<>(courseGrades);
        sortedGrades.sort((a, b) -> {
            java.time.LocalDateTime at = a.getPublishTime() != null ? a.getPublishTime() : java.time.LocalDateTime.MIN;
            java.time.LocalDateTime bt = b.getPublishTime() != null ? b.getPublishTime() : java.time.LocalDateTime.MIN;
            return at.compareTo(bt);
        });
        java.util.List<Map<String, Object>> historyTrend = new java.util.ArrayList<>();
        List<Grade> recentGrades = sortedGrades.stream()
                .skip(Math.max(0, sortedGrades.size() - 10))
                .collect(Collectors.toList());
        for (Grade g : recentGrades) {
            Exam trendExam = examMapper.selectById(g.getExamId());
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("date", g.getPublishTime() != null ? g.getPublishTime().toLocalDate().toString() : "未知");
            item.put("score", g.getTotalScore() != null ? g.getTotalScore() : BigDecimal.ZERO);
            item.put("label", trendExam != null ? trendExam.getName() : "考试");
            historyTrend.add(item);
        }
        result.put("historyTrend", historyTrend);
        return Result.success(result);
    }

    @Operation(summary = "申请成绩复核")
    @PostMapping("/{gradeId}/review")
    public Result<Map<String, Object>> applyReview(
            @PathVariable Long gradeId,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {

        Grade grade = gradeMapper.selectById(gradeId);
        if (grade == null) {
            return Result.error("成绩记录不存在");
        }

        Long studentId = getCurrentUserId(request);
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }
        if (!studentId.equals(grade.getStudentId())) {
            return Result.error("无权复核该成绩");
        }

        Long questionId = parseLong(body.get("questionId"));
        // 检查是否已经申请过同一范围的复核
        LambdaQueryWrapper<GradeReview> existingWrapper = new LambdaQueryWrapper<>();
        existingWrapper.eq(GradeReview::getGradeId, gradeId)
                      .eq(GradeReview::getStudentId, studentId)
                      .in(GradeReview::getStatus, List.of("pending", "approved"));
        if (questionId == null) {
            existingWrapper.isNull(GradeReview::getQuestionId);
        } else {
            existingWrapper.eq(GradeReview::getQuestionId, questionId);
        }
        GradeReview existing = gradeReviewMapper.selectOne(existingWrapper);
        if (existing != null) {
            return Result.error(questionId == null ? "您已申请过整卷复核，请勿重复申请" : "您已申请过本题复核，请勿重复申请");
        }

        // 创建复核申请记录
        GradeReview review = new GradeReview();
        review.setStudentId(studentId);
        review.setExamId(grade.getExamId());
        review.setGradeId(gradeId);
        review.setQuestionId(questionId);
        review.setReason(stringValue(body.get("reason")));
        review.setStudentComment(stringValue(body.get("comment")));
        review.setOriginalScore(grade.getTotalScore());
        review.setStatus("pending");

        gradeReviewMapper.insert(review);

        Map<String, Object> result = new HashMap<>();
        result.put("reviewId", review.getId());
        result.put("message", "复核申请已提交");
        return Result.success(result);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private Long parseLong(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }
}
