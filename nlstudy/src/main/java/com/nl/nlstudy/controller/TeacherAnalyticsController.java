package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.AnswerRecord;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.entity.Grade;
import com.nl.nlstudy.entity.ExamRecord;
import com.nl.nlstudy.mapper.AnswerRecordMapper;
import com.nl.nlstudy.mapper.ExamRecordMapper;
import com.nl.nlstudy.mapper.QuestionMapper;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.ExamMapper;
import com.nl.nlstudy.mapper.GradeMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "教师端-数据分析")
@RestController
@RequestMapping("/teacher/analytics")
@RequiredArgsConstructor
public class TeacherAnalyticsController {

    private final QuestionMapper questionMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final ExamRecordMapper examRecordMapper;
    private final CourseMapper courseMapper;
    private final ExamMapper examMapper;
    private final GradeMapper gradeMapper;
    private final JdbcTemplate jdbcTemplate;

    @Operation(summary = "知识点掌握度")
    @GetMapping("/knowledge/{courseId}")
    public Result<List<Map<String, Object>>> knowledgeMastery(
            @PathVariable Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        // 验证课程是否属于当前教师
        if (teacherId != null) {
            Course course = courseMapper.selectById(courseId);
            if (course == null || !teacherId.equals(course.getTeacherId())) {
                return Result.error("无权访问该课程的知识点掌握度数据");
            }
        }
        // 获取课程下所有题目
        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getCourseId, courseId);
        qWrapper.eq(Question::getStatus, 1);
        List<Question> questions = questionMapper.selectList(qWrapper);

        if (questions == null || questions.isEmpty()) {
            return Result.success(List.of());
        }

        // 按知识点分组
        Map<String, List<Question>> kpGroups = new LinkedHashMap<>();
        for (Question q : questions) {
            if (q.getKnowledgePoints() != null && !q.getKnowledgePoints().isBlank()) {
                String kp = q.getKnowledgePoints().trim();
                kpGroups.computeIfAbsent(kp, k -> new ArrayList<>()).add(q);
            }
        }

        // 获取所有题目的答题记录
        List<Long> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());
        LambdaQueryWrapper<AnswerRecord> arWrapper = new LambdaQueryWrapper<>();
        arWrapper.in(AnswerRecord::getQuestionId, questionIds);
        arWrapper.isNotNull(AnswerRecord::getIsCorrect);
        List<AnswerRecord> answerRecords = answerRecordMapper.selectList(arWrapper);

        // 按知识点统计正确率
        Map<Long, Long> questionCorrectCount = new HashMap<>();
        Map<Long, Long> questionTotalCount = new HashMap<>();
        for (AnswerRecord ar : answerRecords) {
            questionTotalCount.merge(ar.getQuestionId(), 1L, Long::sum);
            if (Boolean.TRUE.equals(ar.getIsCorrect())) {
                questionCorrectCount.merge(ar.getQuestionId(), 1L, Long::sum);
            }
        }

        // 计算每个知识点的掌握度
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Question>> entry : kpGroups.entrySet()) {
            String kpName = entry.getKey();
            List<Question> kpQuestions = entry.getValue();

            int total = 0;
            int correct = 0;
            for (Question q : kpQuestions) {
                Long qTotal = questionTotalCount.getOrDefault(q.getId(), 0L);
                Long qCorrect = questionCorrectCount.getOrDefault(q.getId(), 0L);
                total += qTotal.intValue();
                correct += qCorrect.intValue();
            }

            int mastery = 0;
            if (total > 0) {
                mastery = BigDecimal.valueOf(correct * 100.0 / total)
                        .setScale(0, RoundingMode.HALF_UP).intValue();
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", kpName);
            item.put("value", mastery);
            item.put("totalQuestions", kpQuestions.size());
            item.put("answeredCount", total);
            result.add(item);
        }

        return Result.success(result);
    }

    @Operation(summary = "考试知识点掌握度")
    @GetMapping("/knowledge/exam/{examId}")
    public Result<List<Map<String, Object>>> examKnowledgeMastery(
            @PathVariable Long examId,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null || exam.getExamPaperId() == null) {
            return Result.success(List.of());
        }
        if (teacherId != null && !teacherId.equals(exam.getTeacherId())) {
            return Result.error("无权访问该考试的知识点掌握度数据");
        }

        String paperSql = "SELECT question_id, score FROM exam_paper_question WHERE exam_paper_id = ?";
        List<Map<String, Object>> paperRows = jdbcTemplate.queryForList(paperSql, exam.getExamPaperId());
        if (paperRows.isEmpty()) {
            return Result.success(List.of());
        }

        Map<Long, Double> questionScoreMap = new HashMap<>();
        List<Long> questionIds = new ArrayList<>();
        for (Map<String, Object> row : paperRows) {
            Long questionId = ((Number) row.get("question_id")).longValue();
            double score = row.get("score") == null ? 0.0 : ((Number) row.get("score")).doubleValue();
            questionIds.add(questionId);
            questionScoreMap.put(questionId, score);
        }

        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
        if (questionMap.isEmpty()) {
            return Result.success(List.of());
        }

        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getExamId, examId);
        List<ExamRecord> records = examRecordMapper.selectList(recordWrapper);
        if (records == null || records.isEmpty()) {
            return Result.success(List.of());
        }
        List<Long> recordIds = records.stream().map(ExamRecord::getId).collect(Collectors.toList());

        LambdaQueryWrapper<AnswerRecord> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.in(AnswerRecord::getExamRecordId, recordIds);
        answerWrapper.in(AnswerRecord::getQuestionId, questionIds);
        List<AnswerRecord> answers = answerRecordMapper.selectList(answerWrapper);
        if (answers == null || answers.isEmpty()) {
            return Result.success(List.of());
        }

        Map<String, Double> actualScoreByKnowledge = new LinkedHashMap<>();
        Map<String, Double> fullScoreByKnowledge = new LinkedHashMap<>();
        Map<String, Set<Long>> questionsByKnowledge = new LinkedHashMap<>();
        Map<String, Integer> answeredCountByKnowledge = new LinkedHashMap<>();

        for (AnswerRecord answer : answers) {
            Long questionId = answer.getQuestionId();
            Question question = questionMap.get(questionId);
            if (question == null) {
                continue;
            }
            List<String> knowledgePoints = splitKnowledgePoints(question.getKnowledgePoints());
            if (knowledgePoints.isEmpty()) {
                knowledgePoints = List.of("未标注知识点");
            }
            double actualScore = answer.getScore() == null ? 0.0 : answer.getScore().doubleValue();
            double fullScore = questionScoreMap.getOrDefault(questionId, 0.0);
            for (String knowledge : knowledgePoints) {
                actualScoreByKnowledge.merge(knowledge, actualScore, Double::sum);
                fullScoreByKnowledge.merge(knowledge, fullScore, Double::sum);
                questionsByKnowledge.computeIfAbsent(knowledge, key -> new LinkedHashSet<>()).add(questionId);
                answeredCountByKnowledge.merge(knowledge, 1, Integer::sum);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String knowledge : fullScoreByKnowledge.keySet()) {
            double fullScore = fullScoreByKnowledge.getOrDefault(knowledge, 0.0);
            double actualScore = actualScoreByKnowledge.getOrDefault(knowledge, 0.0);
            double mastery = fullScore > 0 ? actualScore * 100.0 / fullScore : 0.0;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", knowledge);
            item.put("value", BigDecimal.valueOf(mastery).setScale(1, RoundingMode.HALF_UP).doubleValue());
            item.put("mastery", BigDecimal.valueOf(mastery).setScale(1, RoundingMode.HALF_UP).doubleValue());
            item.put("totalQuestions", questionsByKnowledge.getOrDefault(knowledge, Set.of()).size());
            item.put("answeredCount", answeredCountByKnowledge.getOrDefault(knowledge, 0));
            item.put("score", BigDecimal.valueOf(actualScore).setScale(1, RoundingMode.HALF_UP).doubleValue());
            item.put("fullScore", BigDecimal.valueOf(fullScore).setScale(1, RoundingMode.HALF_UP).doubleValue());
            result.add(item);
        }

        result.sort((a, b) -> Double.compare(((Number) b.get("value")).doubleValue(), ((Number) a.get("value")).doubleValue()));
        return Result.success(result);
    }

    @Operation(summary = "考试趋势")
    @GetMapping("/trend/{courseId}")
    public Result<List<Map<String, Object>>> examTrend(
            @PathVariable Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        // 验证课程是否属于当前教师
        if (teacherId != null) {
            Course course = courseMapper.selectById(courseId);
            if (course == null || !teacherId.equals(course.getTeacherId())) {
                return Result.error("无权访问该课程的考试趋势数据");
            }
        }
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
        examWrapper.eq(Exam::getCourseId, courseId)
                .orderByAsc(Exam::getStartTime);
        List<Exam> exams = examMapper.selectList(examWrapper);
        if (exams == null || exams.isEmpty()) {
            return Result.success(List.of());
        }

        List<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toList());
        LambdaQueryWrapper<Grade> gradeWrapper = new LambdaQueryWrapper<>();
        gradeWrapper.in(Grade::getExamId, examIds);
        List<Grade> grades = gradeMapper.selectList(gradeWrapper);
        if (grades == null || grades.isEmpty()) {
            return Result.success(List.of());
        }

        Set<Long> studentIds = grades.stream()
                .map(Grade::getStudentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> studentClassMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            String placeholders = studentIds.stream().map(id -> "?").collect(Collectors.joining(","));
            String sql = "SELECT sc.student_id, c.name FROM student_class sc " +
                    "JOIN sys_class c ON c.id = sc.class_id AND c.deleted = 0 " +
                    "WHERE sc.student_id IN (" + placeholders + ")";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, studentIds.toArray());
            for (Map<String, Object> row : rows) {
                Object sid = row.get("student_id");
                Object cname = row.get("name");
                if (sid != null && cname != null) {
                    studentClassMap.put(((Number) sid).longValue(), String.valueOf(cname));
                }
            }
        }

        Map<String, Map<Long, Double>> classExamAvgMap = new LinkedHashMap<>();
        for (Grade grade : grades) {
            if (grade.getStudentId() == null || grade.getExamId() == null || grade.getTotalScore() == null) {
                continue;
            }
            String className = studentClassMap.getOrDefault(grade.getStudentId(), "未分班");
            classExamAvgMap.computeIfAbsent(className, key -> new LinkedHashMap<>());
        }

        for (String className : classExamAvgMap.keySet()) {
            for (Exam exam : exams) {
                List<Grade> examGrades = grades.stream()
                        .filter(g -> exam.getId().equals(g.getExamId()))
                        .filter(g -> className.equals(studentClassMap.getOrDefault(g.getStudentId(), "未分班")))
                        .filter(g -> g.getTotalScore() != null)
                        .toList();
                double avg = examGrades.stream()
                        .map(Grade::getTotalScore)
                        .mapToDouble(BigDecimal::doubleValue)
                        .average().orElse(0.0);
                classExamAvgMap.get(className).put(exam.getId(), BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue());
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<Long, Double>> entry : classExamAvgMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("className", entry.getKey());
            List<Map<String, Object>> points = new ArrayList<>();
            for (Exam exam : exams) {
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("examId", exam.getId());
                point.put("examName", exam.getName());
                double avgScore = entry.getValue().getOrDefault(exam.getId(), 0.0);
                double totalScore = exam.getTotalScore() == null ? 0.0 : exam.getTotalScore().doubleValue();
                long submittedCount = grades.stream()
                        .filter(g -> exam.getId().equals(g.getExamId()))
                        .filter(g -> entry.getKey().equals(studentClassMap.getOrDefault(g.getStudentId(), "未分班")))
                        .filter(g -> g.getTotalScore() != null)
                        .count();
                double avgRate = totalScore > 0 ? avgScore * 100.0 / totalScore : 0.0;
                point.put("avgScore", avgScore);
                point.put("totalScore", BigDecimal.valueOf(totalScore).setScale(1, RoundingMode.HALF_UP).doubleValue());
                point.put("avgRate", BigDecimal.valueOf(avgRate).setScale(1, RoundingMode.HALF_UP).doubleValue());
                point.put("submittedCount", submittedCount);
                points.add(point);
            }
            item.put("points", points);
            result.add(item);
        }
        return Result.success(result);
    }

    @Operation(summary = "题型难度分析")
    @GetMapping("/difficulty-analysis/{examId}")
    public Result<List<Map<String, Object>>> difficultyAnalysis(
            @PathVariable Long examId,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        if (teacherId != null) {
            Exam exam = examMapper.selectById(examId);
            if (exam == null || !teacherId.equals(exam.getTeacherId())) {
                return Result.error("未找到考试对应的试卷信息");
            }
        }

        Exam exam = examMapper.selectById(examId);
        if (exam == null || exam.getExamPaperId() == null) {
            return Result.success(List.of());
        }

        String paperSql = "SELECT question_id, score FROM exam_paper_question WHERE exam_paper_id = ?";
        List<Map<String, Object>> paperRows = jdbcTemplate.queryForList(paperSql, exam.getExamPaperId());
        if (paperRows.isEmpty()) {
            return Result.success(List.of());
        }

        Map<Long, Double> questionScoreMap = new HashMap<>();
        List<Long> questionIds = new ArrayList<>();
        for (Map<String, Object> row : paperRows) {
            Long questionId = ((Number) row.get("question_id")).longValue();
            Double score = ((Number) row.get("score")).doubleValue();
            questionIds.add(questionId);
            questionScoreMap.put(questionId, score);
        }

        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));

        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getExamId, examId);
        List<ExamRecord> records = examRecordMapper.selectList(recordWrapper);
        if (records == null || records.isEmpty()) {
            return Result.success(List.of());
        }
        List<Long> recordIds = records.stream().map(ExamRecord::getId).collect(Collectors.toList());

        LambdaQueryWrapper<AnswerRecord> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.in(AnswerRecord::getExamRecordId, recordIds);
        List<AnswerRecord> answers = answerRecordMapper.selectList(answerWrapper);
        if (answers == null || answers.isEmpty()) {
            return Result.success(List.of());
        }

        Map<String, List<AnswerRecord>> byType = answers.stream()
                .filter(a -> a.getQuestionId() != null && questionMap.containsKey(a.getQuestionId()))
                .collect(Collectors.groupingBy(a -> questionMap.get(a.getQuestionId()).getType(), LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<AnswerRecord>> entry : byType.entrySet()) {
            String type = entry.getKey();
            List<AnswerRecord> typeAnswers = entry.getValue();
            double actualTotal = typeAnswers.stream()
                    .filter(a -> a.getScore() != null)
                    .mapToDouble(AnswerRecord::getScore)
                    .sum();
            double expectedTotal = typeAnswers.stream()
                    .mapToDouble(a -> questionScoreMap.getOrDefault(a.getQuestionId(), 0.0))
                    .sum();
            double actualRate = expectedTotal > 0 ? actualTotal * 100.0 / expectedTotal : 0.0;
            long questionCount = typeAnswers.stream().map(AnswerRecord::getQuestionId).filter(Objects::nonNull).distinct().count();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", type);
            item.put("typeName", mapQuestionType(type));
            item.put("questionCount", questionCount);
            item.put("actualRate", BigDecimal.valueOf(actualRate).setScale(1, RoundingMode.HALF_UP).doubleValue());
            item.put("expectedRate", estimateExpectedRate(typeAnswers, questionMap, questionScoreMap));
            result.add(item);
        }

        return Result.success(result);
    }

    private String mapQuestionType(String type) {
        if (type == null) return "未知题型";
        return switch (type) {
            case "single_choice" -> "选择题";
            case "multi_choice" -> "多选题";
            case "true_false" -> "判断题";
            case "fill_blank" -> "填空题";
            case "essay" -> "简答题";
            default -> type;
        };
    }

    private double estimateExpectedRate(List<AnswerRecord> answers, Map<Long, Question> questionMap, Map<Long, Double> questionScoreMap) {
        Map<Long, Double> fullScoreByQuestion = new LinkedHashMap<>();
        for (AnswerRecord answer : answers) {
            Long questionId = answer.getQuestionId();
            if (questionId != null && questionMap.containsKey(questionId)) {
                fullScoreByQuestion.put(questionId, questionScoreMap.getOrDefault(questionId, 0.0));
            }
        }
        double weightedTotal = 0.0;
        double scoreTotal = 0.0;
        for (Map.Entry<Long, Double> entry : fullScoreByQuestion.entrySet()) {
            Question question = questionMap.get(entry.getKey());
            double score = entry.getValue();
            weightedTotal += expectedRateByDifficulty(question == null ? null : question.getDifficulty()) * score;
            scoreTotal += score;
        }
        double expectedRate = scoreTotal > 0 ? weightedTotal / scoreTotal : 70.0;
        return BigDecimal.valueOf(expectedRate).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private double expectedRateByDifficulty(String difficulty) {
        if (difficulty == null) return 70.0;
        return switch (difficulty.toLowerCase(Locale.ROOT)) {
            case "easy", "simple", "简单" -> 85.0;
            case "medium", "normal", "中等", "普通" -> 70.0;
            case "hard", "difficult", "困难" -> 55.0;
            default -> 70.0;
        };
    }

    private List<String> splitKnowledgePoints(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split("[,，;；/、\\n\\r]+"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .distinct()
                .toList();
    }

}
