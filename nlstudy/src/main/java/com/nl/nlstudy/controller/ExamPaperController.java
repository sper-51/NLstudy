package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.ExamPaper;
import com.nl.nlstudy.entity.PaperQuestion;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.mapper.ExamPaperMapper;
import com.nl.nlstudy.mapper.PaperQuestionMapper;
import com.nl.nlstudy.mapper.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "教师端-试卷管理")
@RestController
@RequestMapping("/teacher/papers")
@RequiredArgsConstructor
public class ExamPaperController {

    private final ExamPaperMapper examPaperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final CourseMapper courseMapper;

    @Operation(summary = "获取试卷列表")
    @GetMapping
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        Page<ExamPaper> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(ExamPaper::getCourseId, courseId);
        }
        if (status != null) {
            wrapper.eq(ExamPaper::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(ExamPaper::getName, keyword);
        }
        if (teacherId != null) {
            wrapper.eq(ExamPaper::getTeacherId, teacherId);
        }
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        Page<ExamPaper> result = examPaperMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> paperList = new ArrayList<>();
        for (ExamPaper paper : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", paper.getId());
            item.put("name", paper.getName());
            item.put("courseId", paper.getCourseId());
            
            Course course = courseMapper.selectById(paper.getCourseId());
            item.put("courseName", course != null ? course.getName() : "");
            
            item.put("totalScore", paper.getTotalScore());
            item.put("questionCount", paper.getQuestionCount());
            item.put("duration", paper.getDuration());
            item.put("type", paper.getType());
            item.put("status", paper.getStatus());
            item.put("createTime", paper.getCreateTime());
            paperList.add(item);
        }

        return Result.success(PageResult.of(paperList, page, pageSize, result.getTotal()));
    }

    @Operation(summary = "获取试卷详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        ExamPaper paper = examPaperMapper.selectById(id);
        if (paper == null) {
            return Result.error("试卷不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", paper.getId());
        result.put("name", paper.getName());
        result.put("courseId", paper.getCourseId());
        
        Course course = courseMapper.selectById(paper.getCourseId());
        result.put("courseName", course != null ? course.getName() : "");
        
        result.put("totalScore", paper.getTotalScore());
        result.put("passScore", paper.getPassScore());
        result.put("duration", paper.getDuration());
        result.put("questionCount", paper.getQuestionCount());
        result.put("type", paper.getType());
        result.put("status", paper.getStatus());
        result.put("createTime", paper.getCreateTime());

        return Result.success(result);
    }

    @Operation(summary = "创建试卷")
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> body, @RequestHeader("X-User-Id") Long teacherId) {
        ExamPaper paper = new ExamPaper();
        paper.setName((String) body.get("name"));
        paper.setCourseId(((Number) body.get("courseId")).longValue());
        paper.setTeacherId(teacherId);
        paper.setTotalScore(BigDecimal.valueOf(((Number) body.getOrDefault("totalScore", 100)).doubleValue()));
        paper.setPassScore(BigDecimal.valueOf(((Number) body.getOrDefault("passScore", 60)).doubleValue()));
        paper.setDuration((Integer) body.getOrDefault("duration", 90));
        paper.setQuestionCount((Integer) body.getOrDefault("questionCount", 0));
        paper.setType((String) body.getOrDefault("type", "manual"));
        paper.setStatus((Integer) body.getOrDefault("status", 0));
        
        examPaperMapper.insert(paper);

        Map<String, Object> result = new HashMap<>();
        result.put("id", paper.getId());
        result.put("name", paper.getName());
        result.put("courseId", paper.getCourseId());
        result.put("totalScore", paper.getTotalScore());
        result.put("status", paper.getStatus());
        
        return Result.success(result);
    }

    @Operation(summary = "更新试卷")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        ExamPaper paper = examPaperMapper.selectById(id);
        if (paper == null) {
            return Result.error("试卷不存在");
        }

        if (body.containsKey("name")) {
            paper.setName((String) body.get("name"));
        }
        if (body.containsKey("courseId")) {
            paper.setCourseId(((Number) body.get("courseId")).longValue());
        }
        if (body.containsKey("totalScore")) {
            paper.setTotalScore(BigDecimal.valueOf(((Number) body.get("totalScore")).doubleValue()));
        }
        if (body.containsKey("passScore")) {
            paper.setPassScore(BigDecimal.valueOf(((Number) body.get("passScore")).doubleValue()));
        }
        if (body.containsKey("duration")) {
            paper.setDuration((Integer) body.get("duration"));
        }
        if (body.containsKey("questionCount")) {
            paper.setQuestionCount((Integer) body.get("questionCount"));
        }
        if (body.containsKey("type")) {
            paper.setType((String) body.get("type"));
        }
        if (body.containsKey("status")) {
            paper.setStatus((Integer) body.get("status"));
        }

        examPaperMapper.updateById(paper);
        return Result.success();
    }

    @Operation(summary = "删除试卷")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examPaperMapper.deleteById(id);
        paperQuestionMapper.deleteByPaperId(id);
        return Result.success();
    }

    @Operation(summary = "添加题目到试卷")
    @PostMapping("/{paperId}/questions")
    public Result<Void> addQuestions(
            @PathVariable Long paperId,
            @RequestBody Map<String, Object> body) {
        
        List<Number> questionIds = (List<Number>) body.get("questionIds");
        List<Number> scores = (List<Number>) body.get("scores");

        if (questionIds == null || questionIds.isEmpty()) {
            return Result.error("题目列表不能为空");
        }

        paperQuestionMapper.deleteByPaperId(paperId);

        int sortOrder = 1;
        for (int i = 0; i < questionIds.size(); i++) {
            PaperQuestion pq = new PaperQuestion();
            pq.setPaperId(paperId);
            pq.setQuestionId(questionIds.get(i).longValue());
            pq.setScore(BigDecimal.valueOf(scores != null && scores.size() > i ? scores.get(i).doubleValue() : 10));
            pq.setSortOrder(sortOrder++);
            paperQuestionMapper.insert(pq);
        }

        ExamPaper paper = examPaperMapper.selectById(paperId);
        if (paper != null) {
            paper.setQuestionCount(questionIds.size());
            examPaperMapper.updateById(paper);
        }

        return Result.success();
    }

    @Operation(summary = "移除试卷中的题目")
    @DeleteMapping("/{paperId}/questions/{questionId}")
    public Result<Void> removeQuestion(
            @PathVariable Long paperId,
            @PathVariable Long questionId) {
        
        LambdaQueryWrapper<PaperQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperQuestion::getPaperId, paperId);
        wrapper.eq(PaperQuestion::getQuestionId, questionId);
        paperQuestionMapper.delete(wrapper);

        List<PaperQuestion> remaining = paperQuestionMapper.selectByPaperId(paperId);
        ExamPaper paper = examPaperMapper.selectById(paperId);
        if (paper != null) {
            paper.setQuestionCount(remaining.size());
            examPaperMapper.updateById(paper);
        }

        return Result.success();
    }

    @Operation(summary = "预览试卷")
    @GetMapping("/{paperId}/preview")
    public Result<Map<String, Object>> preview(@PathVariable Long paperId) {
        ExamPaper paper = examPaperMapper.selectById(paperId);
        if (paper == null) {
            return Result.error("试卷不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", paper.getId());
        result.put("name", paper.getName());
        result.put("courseId", paper.getCourseId());
        
        Course course = courseMapper.selectById(paper.getCourseId());
        result.put("courseName", course != null ? course.getName() : "");
        
        result.put("totalScore", paper.getTotalScore());
        result.put("duration", paper.getDuration());

        List<Question> questions = paperQuestionMapper.selectQuestionsByPaperId(paperId);
        List<Map<String, Object>> questionList = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> qItem = new HashMap<>();
            qItem.put("id", q.getId());
            qItem.put("type", q.getType());
            qItem.put("content", q.getContent());
            qItem.put("score", q.getScore());
            qItem.put("answer", q.getAnswer());
            qItem.put("analysis", q.getAnalysis());
            qItem.put("knowledgePoints", q.getKnowledgePoints());
            questionList.add(qItem);
        }
        result.put("questions", questionList);

        return Result.success(result);
    }
}