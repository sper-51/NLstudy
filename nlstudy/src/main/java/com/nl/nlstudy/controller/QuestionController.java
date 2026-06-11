package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.entity.QuestionOption;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.QuestionMapper;
import com.nl.nlstudy.mapper.QuestionOptionMapper;
import com.nl.nlstudy.service.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "教师端-题库管理")
@RestController
@RequestMapping("/teacher/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final CourseMapper courseMapper;

    @Operation(summary = "获取题目列表（分页）")
    @GetMapping
    public Result<PageResult<Question>> list(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {

        Page<Question> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        if (courseId != null) {
            wrapper.eq(Question::getCourseId, courseId);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Question::getType, type);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Question::getContent, keyword);
        }
        if (teacherId != null) {
            wrapper.eq(Question::getTeacherId, teacherId);
        }
        wrapper.orderByDesc(Question::getCreateTime);
        
        Page<Question> result = questionService.page(pageParam, wrapper);
        return Result.success(PageResult.of(result.getRecords(), page, pageSize, result.getTotal()));
    }

    @Operation(summary = "获取题目列表（组卷专用-含课程名称）")
    @GetMapping("/for-paper")
    public Result<Map<String, Object>> listForPaper(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        if (courseId != null) {
            wrapper.eq(Question::getCourseId, courseId);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Question::getType, type);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Question::getContent, keyword);
        }
        if (teacherId != null) {
            wrapper.eq(Question::getTeacherId, teacherId);
        }
        wrapper.orderByDesc(Question::getCreateTime);
        
        List<Question> questions = questionMapper.selectList(wrapper);
        
        // 查询课程信息
        Set<Long> courseIds = questions.stream()
            .map(Question::getCourseId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        final Map<Long, String> courseNameMap;
        if (!courseIds.isEmpty()) {
            List<Course> courses = courseMapper.selectBatchIds(courseIds);
            courseNameMap = courses.stream()
                    .collect(Collectors.toMap(Course::getId, Course::getName));
        } else {
            courseNameMap = Map.of();
        }
        
        // 转换数据
        List<Map<String, Object>> questionList = questions.stream().map(q -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", q.getId());
            item.put("content", q.getContent());
            item.put("type", q.getType());
            item.put("typeName", getTypeName(q.getType()));
            item.put("difficulty", q.getDifficulty());
            item.put("difficultyName", getDifficultyName(q.getDifficulty()));
            item.put("score", q.getScore());
            item.put("assignedScore", q.getScore());
            item.put("courseName", courseNameMap.getOrDefault(q.getCourseId(), ""));
            item.put("knowledge", q.getKnowledgePoints() != null ? q.getKnowledgePoints() : "");
            return item;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("questions", questionList);
        result.put("total", questionList.size());
        
        return Result.success(result);
    }

    @Operation(summary = "获取知识点列表")
    @GetMapping("/knowledge-points")
    public Result<List<Map<String, Object>>> getKnowledgePoints(
            @RequestParam(required = false) Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(Question::getCourseId, courseId);
        }
        if (teacherId != null) {
            wrapper.eq(Question::getTeacherId, teacherId);
        }
        wrapper.isNotNull(Question::getKnowledgePoints);
        wrapper.ne(Question::getKnowledgePoints, "");
        wrapper.select(Question::getKnowledgePoints);
        
        List<Question> questions = questionMapper.selectList(wrapper);
        
        // 提取并去重知识点
        Set<String> knowledgeSet = new LinkedHashSet<>();
        for (Question q : questions) {
            if (q.getKnowledgePoints() != null) {
                for (String kp : q.getKnowledgePoints().split("[,，;；]")) {
                    String trimmed = kp.trim();
                    if (!trimmed.isEmpty()) {
                        knowledgeSet.add(trimmed);
                    }
                }
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        int idx = 1;
        for (String name : knowledgeSet) {
            result.add(Map.of("value", name, "label", name));
            idx++;
        }
        
        return Result.success(result);
    }

    private String getTypeName(String type) {
        if (type == null) return "其他";
        return switch (type) {
            case "single_choice" -> "单选题";
            case "multi_choice" -> "多选题";
            case "true_false" -> "判断题";
            case "fill_blank" -> "填空题";
            case "essay" -> "简答题";
            case "code" -> "编程题";
            default -> type;
        };
    }

    private String getDifficultyName(String difficulty) {
        if (difficulty == null) return "中等";
        return switch (difficulty) {
            case "easy" -> "简单";
            case "medium" -> "中等";
            case "hard" -> "困难";
            default -> difficulty;
        };
    }

    @Operation(summary = "获取题目详情（含选项）")
    @GetMapping("/{id}")
    public Result<Object> getDetail(@PathVariable Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }
        
        if ("single_choice".equals(question.getType()) || "multi_choice".equals(question.getType())) {
            List<QuestionOption> options = questionOptionMapper.selectList(
                new LambdaQueryWrapper<QuestionOption>()
                    .eq(QuestionOption::getQuestionId, id)
                    .orderByAsc(QuestionOption::getSortOrder)
            );
            return Result.success(java.util.Map.of(
                "question", question,
                "options", options
            ));
        }
        return Result.success(java.util.Map.of("question", question));
    }

    @Operation(summary = "创建题目")
    @PostMapping
    public Result<Object> create(@RequestBody java.util.Map<String, Object> body, @RequestHeader("X-User-Id") Long teacherId) {
        Question question = new Question();
        question.setCourseId(body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null);
        question.setTeacherId(teacherId);
        question.setType((String) body.get("type"));
        question.setDifficulty((String) body.getOrDefault("difficulty", "medium"));
        question.setContent((String) body.get("content"));
        question.setAnswer((String) body.get("answer"));
        question.setAnalysis((String) body.get("analysis"));
        if (body.get("score") != null) {
            question.setScore(new java.math.BigDecimal(body.get("score").toString()));
        }
        question.setKnowledgePoints((String) body.get("knowledgePoints"));
        question.setStatus(1);
        
        questionMapper.insert(question);
        
        if (body.containsKey("options")) {
            List<java.util.Map<String, Object>> options = (List<java.util.Map<String, Object>>) body.get("options");
            for (int i = 0; i < options.size(); i++) {
                java.util.Map<String, Object> opt = options.get(i);
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionLabel((String) opt.get("label"));
                option.setOptionContent((String) opt.get("content"));
                option.setIsCorrect(Boolean.TRUE.equals(opt.get("isCorrect")) ? 1 : 0);
                option.setSortOrder(i + 1);
                questionOptionMapper.insert(option);
            }
        }
        
        return Result.success(java.util.Map.of("id", question.getId()));
    }

    @Operation(summary = "更新题目")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody java.util.Map<String, Object> body) {
        Question question = new Question();
        question.setId(id);
        if (body.containsKey("content")) question.setContent((String) body.get("content"));
        if (body.containsKey("answer")) question.setAnswer((String) body.get("answer"));
        if (body.containsKey("analysis")) question.setAnalysis((String) body.get("analysis"));
        if (body.containsKey("difficulty")) question.setDifficulty((String) body.get("difficulty"));
        if (body.containsKey("score")) question.setScore(new java.math.BigDecimal(body.get("score").toString()));
        if (body.containsKey("knowledgePoints")) question.setKnowledgePoints((String) body.get("knowledgePoints"));
        if (body.containsKey("status")) question.setStatus(Integer.valueOf(body.get("status").toString()));
        
        questionMapper.updateById(question);
        
        if (body.containsKey("options")) {
            questionOptionMapper.delete(new LambdaQueryWrapper<QuestionOption>()
                .eq(QuestionOption::getQuestionId, id));
            
            List<java.util.Map<String, Object>> options = (List<java.util.Map<String, Object>>) body.get("options");
            for (int i = 0; i < options.size(); i++) {
                java.util.Map<String, Object> opt = options.get(i);
                QuestionOption option = new QuestionOption();
                option.setQuestionId(id);
                option.setOptionLabel((String) opt.get("label"));
                option.setOptionContent((String) opt.get("content"));
                option.setIsCorrect(Boolean.TRUE.equals(opt.get("isCorrect")) ? 1 : 0);
                option.setSortOrder(i + 1);
                questionOptionMapper.insert(option);
            }
        }
        
        return Result.success();
    }

    @Operation(summary = "删除题目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionOptionMapper.delete(new LambdaQueryWrapper<QuestionOption>()
            .eq(QuestionOption::getQuestionId, id));
        questionService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "收藏题目")
    @PostMapping("/{id}/favorite")
    public Result<Map<String, Object>> favorite(@PathVariable Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }

        // 切换收藏状态（假设 status=2 表示已收藏）
        int newStatus = (question.getStatus() != null && question.getStatus() == 2) ? 1 : 2;
        Question updateQuestion = new Question();
        updateQuestion.setId(id);
        updateQuestion.setStatus(newStatus);
        questionMapper.updateById(updateQuestion);

        Map<String, Object> result = new HashMap<>();
        result.put("isFavorited", newStatus == 2);
        return Result.success(result);
    }

    @Operation(summary = "导入题目")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long courseId) {
        // TODO: 解析Excel导入题目
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("success", 0);
        result.put("failed", 0);
        result.put("message", "批量导入功能开发中，请稍后再试");
        return Result.success(result);
    }
}
