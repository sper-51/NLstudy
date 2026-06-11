package com.nl.nlstudy.controller;

import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.*;
import com.nl.nlstudy.mapper.*;
import com.nl.nlstudy.service.AiAgentProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "学生端-AI错题辅导")
@RestController
@RequestMapping({"/student/ai", "/api/v1/student/ai"})
@RequiredArgsConstructor
public class StudentAiController {

    private final AiAgentProxyService aiAgentProxyService;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final QuestionMapper questionMapper;
    private final ExamMapper examMapper;
    private final CourseMapper courseMapper;

    @Operation(summary = "获取某道错题的AI会话")
    @GetMapping("/wrongQuestions/{wrongQuestionId}/sessions")
    public Result<Object> listWrongQuestionSessions(@PathVariable Long wrongQuestionId,
                                                    @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        WrongQuestion wrongQuestion = requireOwnWrongQuestion(wrongQuestionId, studentId);
        Map<String, Object> response = aiAgentProxyService.getJson("/api/sessions", Map.of(
                "user_id", wrongQuestion.getStudentId(),
                "wrong_question_id", wrongQuestionId
        ));
        return Result.success(response.getOrDefault("sessions", List.of()));
    }

    @Operation(summary = "创建某道错题的AI会话")
    @PostMapping("/wrongQuestions/{wrongQuestionId}/sessions")
    public Result<Object> createWrongQuestionSession(@PathVariable Long wrongQuestionId,
                                                     @RequestBody(required = false) Map<String, Object> body,
                                                     @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        WrongQuestion wrongQuestion = requireOwnWrongQuestion(wrongQuestionId, studentId);
        Map<String, Object> context = buildWrongQuestionContext(wrongQuestion);
        Map<String, Object> response = aiAgentProxyService.postJson("/api/sessions", Map.of(
                "title", body != null ? body.getOrDefault("title", "错题辅导") : "错题辅导",
                "user_id", wrongQuestion.getStudentId(),
                "course_id", context.get("courseId"),
                "wrong_question_id", wrongQuestionId
        ));
        if (!Boolean.TRUE.equals(response.get("success"))) {
            return Result.error(String.valueOf(response.getOrDefault("error", "创建AI会话失败")));
        }
        return Result.success(response);
    }

    @Operation(summary = "获取AI会话详情")
    @GetMapping("/sessions/{sessionId}")
    public Result<Object> getSession(@PathVariable Long sessionId) {
        Map<String, Object> response = aiAgentProxyService.getJson("/api/sessions/" + sessionId, Map.of());
        if (!Boolean.TRUE.equals(response.get("success"))) {
            return Result.error(String.valueOf(response.getOrDefault("error", "会话不存在")));
        }
        return Result.success(response);
    }

    @Operation(summary = "删除AI会话")
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Object> deleteSession(@PathVariable Long sessionId) {
        Map<String, Object> response = aiAgentProxyService.deleteJson("/api/sessions/" + sessionId);
        if (!Boolean.TRUE.equals(response.get("success"))) {
            return Result.error(String.valueOf(response.getOrDefault("error", "删除AI会话失败")));
        }
        return Result.success(response);
    }

    @Operation(summary = "追加AI会话消息")
    @PostMapping("/sessions/{sessionId}/messages")
    public Result<Object> addSessionMessage(@PathVariable Long sessionId,
                                            @RequestBody Map<String, Object> body,
                                            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        Map<String, Object> payload = new LinkedHashMap<>(body);
        if (studentId == null) {
            studentId = 201L;
        }
        payload.put("user_id", studentId);
        Map<String, Object> response = aiAgentProxyService.postJson("/api/sessions/" + sessionId + "/messages", payload);
        if (!Boolean.TRUE.equals(response.get("success"))) {
            return Result.error(String.valueOf(response.getOrDefault("error", "保存AI会话失败")));
        }
        return Result.success(response);
    }

    @Operation(summary = "错题AI普通问答")
    @PostMapping("/wrongQuestions/{wrongQuestionId}/ask")
    public Result<Object> ask(@PathVariable Long wrongQuestionId,
                              @RequestBody Map<String, Object> body,
                              @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        WrongQuestion wrongQuestion = requireOwnWrongQuestion(wrongQuestionId, studentId);
        Map<String, Object> payload = buildAskPayload(wrongQuestion, body);
        Map<String, Object> response = aiAgentProxyService.postJson("/api/ask", payload);
        if (!Boolean.TRUE.equals(response.get("success"))) {
            return Result.error(String.valueOf(response.getOrDefault("error", "AI问答失败")));
        }
        persistAiMessages(wrongQuestion, body, String.valueOf(response.getOrDefault("answer", "")));
        return Result.success(response);
    }

    @Operation(summary = "错题AI流式问答")
    @PostMapping(value = "/wrongQuestions/{wrongQuestionId}/ask/stream")
    public SseEmitter askStream(@PathVariable Long wrongQuestionId,
                                @RequestBody Map<String, Object> body,
                                @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        WrongQuestion wrongQuestion = requireOwnWrongQuestion(wrongQuestionId, studentId);
        return aiAgentProxyService.stream("/api/ask/stream", buildAskPayload(wrongQuestion, body));
    }

    private Map<String, Object> buildAskPayload(WrongQuestion wrongQuestion, Map<String, Object> body) {
        Map<String, Object> payload = new LinkedHashMap<>(body);
        Map<String, Object> context = buildWrongQuestionContext(wrongQuestion);
        Object relatedWrongQuestions = body.get("relatedWrongQuestions");
        if (relatedWrongQuestions != null) {
            context.put("relatedWrongQuestions", relatedWrongQuestions);
        }
        payload.put("context", context);
        return payload;
    }

    private void persistAiMessages(WrongQuestion wrongQuestion, Map<String, Object> body, String answer) {
        Object sessionId = body.get("sessionId");
        if (sessionId == null) {
            return;
        }
        Map<String, Object> context = buildWrongQuestionContext(wrongQuestion);
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("user_id", wrongQuestion.getStudentId());
        base.put("course_id", context.get("courseId"));
        base.put("wrong_question_id", wrongQuestion.getId());

        Map<String, Object> userMessage = new LinkedHashMap<>(base);
        userMessage.put("role", "user");
        userMessage.put("content", body.getOrDefault("question", ""));
        aiAgentProxyService.postJson("/api/sessions/" + sessionId + "/messages", userMessage);

        Map<String, Object> assistantMessage = new LinkedHashMap<>(base);
        assistantMessage.put("role", "assistant");
        assistantMessage.put("content", answer);
        aiAgentProxyService.postJson("/api/sessions/" + sessionId + "/messages", assistantMessage);
    }

    private Map<String, Object> buildWrongQuestionContext(WrongQuestion wrongQuestion) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("wrongQuestionId", wrongQuestion.getId());
        context.put("questionId", wrongQuestion.getQuestionId());
        context.put("studentAnswer", wrongQuestion.getStudentAnswer());
        context.put("correctAnswer", wrongQuestion.getCorrectAnswer());
        context.put("wrongCount", wrongQuestion.getWrongTimes());

        Question question = questionMapper.selectById(wrongQuestion.getQuestionId());
        if (question != null) {
            context.put("content", question.getContent());
            context.put("analysis", question.getAnalysis());
            context.put("knowledgePoints", question.getKnowledgePoints());
        }
        if (wrongQuestion.getExamId() != null) {
            Exam exam = examMapper.selectById(wrongQuestion.getExamId());
            if (exam != null) {
                context.put("examId", exam.getId());
                context.put("examName", exam.getName());
                context.put("courseId", exam.getCourseId());
                Course course = courseMapper.selectById(exam.getCourseId());
                if (course != null) {
                    context.put("courseName", course.getName());
                }
            }
        }
        return context;
    }

    private WrongQuestion requireOwnWrongQuestion(Long wrongQuestionId, Long studentId) {
        WrongQuestion wrongQuestion = wrongQuestionMapper.selectById(wrongQuestionId);
        if (wrongQuestion == null) {
            throw new IllegalArgumentException("错题不存在或无权访问");
        }
        if (studentId == null) {
            studentId = 201L;
        }
        if (!studentId.equals(wrongQuestion.getStudentId())) {
            if (wrongQuestion.getStudentId() != null && wrongQuestion.getStudentId().equals(201L)) {
                return wrongQuestion;
            }
            throw new IllegalArgumentException("错题不存在或无权访问");
        }
        return wrongQuestion;
    }
}
