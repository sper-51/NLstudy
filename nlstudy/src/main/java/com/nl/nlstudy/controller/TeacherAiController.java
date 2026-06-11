package com.nl.nlstudy.controller;

import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.PaperQuestion;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.entity.QuestionOption;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.PaperQuestionMapper;
import com.nl.nlstudy.mapper.QuestionMapper;
import com.nl.nlstudy.mapper.QuestionOptionMapper;
import com.nl.nlstudy.service.AiAgentProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "教师端-AI出题")
@RestController
@RequestMapping({"/teacher/ai/questions", "/api/v1/teacher/ai/questions"})
@RequiredArgsConstructor
public class TeacherAiController {

    private final AiAgentProxyService aiAgentProxyService;
    private final CourseMapper courseMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final PaperQuestionMapper paperQuestionMapper;

    @Operation(summary = "AI生成题目草稿")
    @PostMapping("/generate")
    public Result<Object> generate(@RequestBody Map<String, Object> body,
                                   @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        Map<String, Object> payload = new LinkedHashMap<>(body);
        Long courseId = toLong(body.get("courseId"));
        if (courseId != null) {
            Course course = courseMapper.selectById(courseId);
            if (course == null || (teacherId != null && !teacherId.equals(course.getTeacherId()))) {
                return Result.error(403, "无权访问该课程");
            }
            payload.putIfAbsent("courseName", course.getName());
        }
        Map<String, Object> response = aiAgentProxyService.postJson("/api/teacher/questions/generate", payload);
        if (!Boolean.TRUE.equals(response.get("success"))) {
            return Result.error(String.valueOf(response.getOrDefault("error", "AI出题失败")));
        }
        return Result.success(Map.of("questions", response.getOrDefault("questions", List.of())));
    }

    @Operation(summary = "保存AI题目草稿到题库")
    @PostMapping("/save")
    public Result<Map<String, Object>> save(@RequestBody Map<String, Object> body,
                                            @RequestHeader("X-User-Id") Long teacherId) {
        Long courseId = toLong(body.get("courseId"));
        Course course = courseMapper.selectById(courseId);
        if (course == null || !teacherId.equals(course.getTeacherId())) {
            return Result.error(403, "无权访问该课程");
        }
        List<Map<String, Object>> drafts = (List<Map<String, Object>>) body.getOrDefault("questions", List.of());
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> draft : drafts) {
            ids.add(insertQuestion(courseId, teacherId, draft));
        }
        return Result.success(Map.of("questionIds", ids));
    }

    @Operation(summary = "保存AI题目草稿并加入试卷")
    @PostMapping("/add-to-paper")
    public Result<Map<String, Object>> addToPaper(@RequestBody Map<String, Object> body,
                                                  @RequestHeader("X-User-Id") Long teacherId) {
        Long paperId = toLong(body.get("paperId"));
        Long courseId = toLong(body.get("courseId"));
        Result<Map<String, Object>> saved = save(body, teacherId);
        if (saved.getCode() != 200) {
            return saved;
        }
        List<Long> questionIds = (List<Long>) saved.getData().get("questionIds");
        int sortOrder = paperQuestionMapper.selectByPaperId(paperId).size() + 1;
        for (Long questionId : questionIds) {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(questionId);
            Question question = questionMapper.selectById(questionId);
            paperQuestion.setScore(question != null && question.getScore() != null ? question.getScore() : BigDecimal.TEN);
            paperQuestion.setSortOrder(sortOrder++);
            paperQuestionMapper.insert(paperQuestion);
        }
        return Result.success(Map.of("questionIds", questionIds, "paperId", paperId, "courseId", courseId));
    }

    private Long insertQuestion(Long courseId, Long teacherId, Map<String, Object> draft) {
        Question question = new Question();
        question.setCourseId(courseId);
        question.setTeacherId(teacherId);
        question.setType(normalizeType((String) draft.getOrDefault("type", "essay")));
        question.setDifficulty((String) draft.getOrDefault("difficulty", "medium"));
        question.setContent((String) draft.getOrDefault("content", ""));
        question.setAnswer(String.valueOf(draft.getOrDefault("answer", "")));
        question.setAnalysis((String) draft.getOrDefault("analysis", ""));
        question.setScore(new BigDecimal(String.valueOf(draft.getOrDefault("score", "5"))));
        question.setKnowledgePoints((String) draft.getOrDefault("knowledgePoints", ""));
        question.setStatus(1);
        question.setSource("ai");
        questionMapper.insert(question);

        List<Map<String, Object>> options = (List<Map<String, Object>>) draft.get("options");
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                Map<String, Object> item = options.get(i);
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionLabel(String.valueOf(item.getOrDefault("label", String.valueOf((char) ('A' + i)))));
                option.setOptionContent(String.valueOf(item.getOrDefault("content", "")));
                option.setIsCorrect(Boolean.TRUE.equals(item.get("isCorrect")) ? 1 : 0);
                option.setSortOrder(i + 1);
                questionOptionMapper.insert(option);
            }
        }
        return question.getId();
    }

    private Long toLong(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }

    private String normalizeType(String type) {
        return switch (type) {
            case "single" -> "single_choice";
            case "multiple" -> "multi_choice";
            case "judge" -> "true_false";
            case "fill" -> "fill_blank";
            default -> type;
        };
    }
}
