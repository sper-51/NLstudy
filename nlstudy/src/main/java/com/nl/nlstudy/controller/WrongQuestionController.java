package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.entity.QuestionOption;
import com.nl.nlstudy.entity.WrongQuestion;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.ExamMapper;
import com.nl.nlstudy.mapper.QuestionOptionMapper;
import com.nl.nlstudy.mapper.QuestionMapper;
import com.nl.nlstudy.mapper.WrongQuestionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "学生端-错题本")
@RestController
@RequestMapping("/student/wrongQuestions")
@RequiredArgsConstructor
public class WrongQuestionController {

    private final WrongQuestionMapper wrongQuestionMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final ExamMapper examMapper;
    private final CourseMapper courseMapper;

    @Operation(summary = "获取错题列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String sourceType) {
        if (studentId == null) studentId = 201L;

        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId, studentId);
        if (sourceType != null && !sourceType.isBlank()) {
            wrapper.eq(WrongQuestion::getSourceType, sourceType);
        }
        wrapper.orderByDesc(WrongQuestion::getLastWrongTime);

        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectList(wrapper);

        List<Map<String, Object>> result = wrongQuestions.stream().map(wq -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", wq.getId());
            map.put("wrongQuestionId", wq.getId());
            map.put("questionId", wq.getQuestionId());
            map.put("studentAnswer", wq.getStudentAnswer());
            map.put("myAnswer", wq.getStudentAnswer());
            map.put("correctAnswer", wq.getCorrectAnswer());
            map.put("mySolution", wq.getMySolution());
            map.put("sourceType", wq.getSourceType());
            map.put("examId", wq.getExamId());
            map.put("wrongTimes", wq.getWrongTimes());
            map.put("wrongCount", wq.getWrongTimes());
            map.put("lastWrongTime", wq.getLastWrongTime());
            map.put("isFavorited", wq.getIsFavorited());
            map.put("practiceCount", wq.getPracticeCount());
            map.put("correctCount", wq.getCorrectCount());

            // 获取题目信息
            Question question = questionMapper.selectById(wq.getQuestionId());
            if (question != null) {
                map.put("content", question.getContent());
                map.put("type", question.getType());
                map.put("difficulty", question.getDifficulty());
                map.put("analysis", question.getAnalysis());
                map.put("knowledgePoints", question.getKnowledgePoints());
                map.put("knowledgePoint", question.getKnowledgePoints());
                map.put("answer", question.getAnswer());
                List<QuestionOption> options = questionOptionMapper.selectList(new LambdaQueryWrapper<QuestionOption>()
                        .eq(QuestionOption::getQuestionId, question.getId())
                        .orderByAsc(QuestionOption::getSortOrder));
                map.put("options", options.stream().map(option -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("label", option.getOptionLabel());
                    item.put("content", option.getOptionContent());
                    item.put("isCorrect", option.getIsCorrect() != null && option.getIsCorrect() == 1);
                    return item;
                }).collect(Collectors.toList()));
            }
            if (wq.getExamId() != null) {
                Exam exam = examMapper.selectById(wq.getExamId());
                if (exam != null) {
                    map.put("examName", exam.getName());
                    if (exam.getCourseId() != null) {
                        map.put("courseId", exam.getCourseId());
                        Course course = courseMapper.selectById(exam.getCourseId());
                        if (course != null) {
                            map.put("courseName", course.getName());
                        }
                    }
                }
            }
            return map;
        }).filter(map -> courseId == null || Objects.equals(map.get("courseId"), courseId)).collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "获取错题详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        WrongQuestion wq = wrongQuestionMapper.selectById(id);
        if (wq == null) {
            return Result.error("错题不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", wq.getId());
        result.put("wrongQuestionId", wq.getId());
        result.put("questionId", wq.getQuestionId());
        result.put("studentAnswer", wq.getStudentAnswer());
        result.put("correctAnswer", wq.getCorrectAnswer());
        result.put("mySolution", wq.getMySolution());
        result.put("wrongTimes", wq.getWrongTimes());
        result.put("wrongCount", wq.getWrongTimes());
        result.put("practiceCount", wq.getPracticeCount());
        result.put("correctCount", wq.getCorrectCount());
        result.put("lastWrongTime", wq.getLastWrongTime());
        result.put("isFavorited", wq.getIsFavorited());

        Question question = questionMapper.selectById(wq.getQuestionId());
        if (question != null) {
            result.put("content", question.getContent());
            result.put("type", question.getType());
            result.put("difficulty", question.getDifficulty());
            result.put("analysis", question.getAnalysis());
            result.put("knowledgePoints", question.getKnowledgePoints());
            List<QuestionOption> options = questionOptionMapper.selectList(new LambdaQueryWrapper<QuestionOption>()
                    .eq(QuestionOption::getQuestionId, question.getId())
                    .orderByAsc(QuestionOption::getSortOrder));
            result.put("options", options.stream().map(option -> {
                Map<String, Object> item = new HashMap<>();
                item.put("label", option.getOptionLabel());
                item.put("content", option.getOptionContent());
                item.put("isCorrect", option.getIsCorrect() != null && option.getIsCorrect() == 1);
                return item;
            }).collect(Collectors.toList()));
        }
        if (wq.getExamId() != null) {
            Exam exam = examMapper.selectById(wq.getExamId());
            if (exam != null) {
                result.put("examName", exam.getName());
                if (exam.getCourseId() != null) {
                    Course course = courseMapper.selectById(exam.getCourseId());
                    if (course != null) {
                        result.put("courseName", course.getName());
                    }
                }
            }
        }

        return Result.success(result);
    }

    @Operation(summary = "收藏/取消收藏错题")
    @PostMapping("/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        WrongQuestion wq = wrongQuestionMapper.selectById(id);
        if (wq == null) {
            return Result.error("错题不存在");
        }

        wq.setIsFavorited(wq.getIsFavorited() != null && wq.getIsFavorited() == 1 ? 0 : 1);
        wrongQuestionMapper.updateById(wq);

        return Result.success(wq.getIsFavorited() == 1 ? "已收藏" : "已取消收藏", null);
    }

    @Operation(summary = "删除错题")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        WrongQuestion wq = wrongQuestionMapper.selectById(id);
        if (wq == null) {
            return Result.error("错题不存在");
        }
        if (studentId != null && wq.getStudentId() != null && !studentId.equals(wq.getStudentId())) {
            return Result.error(403, "无权删除该错题");
        }
        wrongQuestionMapper.deleteById(id);
        return Result.success("错题已删除", null);
    }

    @Operation(summary = "错题重练")
    @PostMapping("/{id}/practice")
    public Result<Map<String, Object>> practice(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        WrongQuestion wq = wrongQuestionMapper.selectById(id);
        if (wq == null) {
            return Result.error("错题不存在");
        }

        String answer = body.get("answer");
        Question question = questionMapper.selectById(wq.getQuestionId());

        // 更新练习次数
        wq.setPracticeCount(wq.getPracticeCount() != null ? wq.getPracticeCount() + 1 : 1);

        boolean isCorrect = question != null && answer != null && answer.equals(question.getAnswer());

        if (isCorrect) {
            wq.setCorrectCount(wq.getCorrectCount() != null ? wq.getCorrectCount() + 1 : 1);
            wq.setWrongTimes(Math.max(0, (wq.getWrongTimes() != null ? wq.getWrongTimes() : 1) - 1));
        } else {
            wq.setWrongTimes(wq.getWrongTimes() != null ? wq.getWrongTimes() + 1 : 1);
            wq.setLastWrongTime(LocalDateTime.now());
        }

        wrongQuestionMapper.updateById(wq);

        // 返回练习结果
        Map<String, Object> result = new HashMap<>();
        result.put("isCorrect", isCorrect);
        result.put("studentAnswer", answer);
        result.put("correctAnswer", question != null ? question.getAnswer() : "");
        result.put("analysis", question != null ? question.getAnalysis() : "");
        result.put("wrongTimes", wq.getWrongTimes());
        result.put("wrongCount", wq.getWrongTimes());
        result.put("correctCount", wq.getCorrectCount());
        result.put("practiceCount", wq.getPracticeCount());

        return Result.success(result);
    }

    @Operation(summary = "获取错题报告")
    @GetMapping("/report")
    public Result<Map<String, Object>> report(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) studentId = 201L;

        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId, studentId);
        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();

        // 基本统计
        result.put("totalWrong", wrongQuestions.size());
        result.put("totalPractice", wrongQuestions.stream()
                .mapToInt(wq -> wq.getPracticeCount() != null ? wq.getPracticeCount() : 0).sum());
        result.put("totalCorrect", wrongQuestions.stream()
                .mapToInt(wq -> wq.getCorrectCount() != null ? wq.getCorrectCount() : 0).sum());

        // 正确率
        int totalPractice = (int) result.get("totalPractice");
        int totalCorrect = (int) result.get("totalCorrect");
        result.put("accuracy", totalPractice > 0 ? (double) totalCorrect / totalPractice * 100 : 0);

        // 收藏数
        result.put("favoriteCount", wrongQuestions.stream()
                .filter(wq -> wq.getIsFavorited() != null && wq.getIsFavorited() == 1).count());

        // 按知识点统计
        Map<String, List<WrongQuestion>> byKnowledge = new HashMap<>();
        for (WrongQuestion wq : wrongQuestions) {
            Question q = questionMapper.selectById(wq.getQuestionId());
            if (q != null && q.getKnowledgePoints() != null) {
                String kp = q.getKnowledgePoints();
                byKnowledge.computeIfAbsent(kp, k -> new ArrayList<>()).add(wq);
            }
        }

        List<Map<String, Object>> knowledgeStats = byKnowledge.entrySet().stream().map(entry -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("knowledgePoint", entry.getKey());
            stat.put("wrongCount", entry.getValue().size());
            stat.put("practiceCount", entry.getValue().stream()
                    .mapToInt(wq -> wq.getPracticeCount() != null ? wq.getPracticeCount() : 0).sum());
            stat.put("correctCount", entry.getValue().stream()
                    .mapToInt(wq -> wq.getCorrectCount() != null ? wq.getCorrectCount() : 0).sum());
            int practice = (int) stat.get("practiceCount");
            int correct = (int) stat.get("correctCount");
            stat.put("accuracy", practice > 0 ? (double) correct / practice * 100 : 0);
            return stat;
        }).sorted((a, b) -> ((Number) b.get("wrongCount")).intValue() - ((Number) a.get("wrongCount")).intValue())
          .collect(Collectors.toList());

        result.put("knowledgeStats", knowledgeStats);

        // 按难度统计
        Map<String, Long> byDifficulty = new HashMap<>();
        for (WrongQuestion wq : wrongQuestions) {
            Question q = questionMapper.selectById(wq.getQuestionId());
            if (q != null && q.getDifficulty() != null) {
                byDifficulty.merge(q.getDifficulty(), 1L, Long::sum);
            }
        }
        result.put("byDifficulty", byDifficulty);

        // 趋势数据（最近7天）
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            int dayWrong = (int) wrongQuestions.stream()
                    .filter(wq -> wq.getLastWrongTime() != null &&
                            wq.getLastWrongTime().isAfter(startOfDay) &&
                            wq.getLastWrongTime().isBefore(endOfDay))
                    .count();

            trend.add(Map.of("date", date.toLocalDate().toString(), "wrongCount", dayWrong));
        }
        result.put("trend", trend);

        return Result.success(result);
    }
}
