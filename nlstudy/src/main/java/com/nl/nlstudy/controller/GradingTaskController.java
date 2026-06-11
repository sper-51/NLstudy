package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.*;
import com.nl.nlstudy.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "教师端-试卷批改")
@RestController
@RequestMapping("/teacher/grading")
@RequiredArgsConstructor
public class GradingTaskController {

    private final GradingTaskMapper gradingTaskMapper;
    private final GradeReviewMapper gradeReviewMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamMapper examMapper;
    private final QuestionMapper questionMapper;
    private final SysUserMapper sysUserMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final GradeMapper gradeMapper;
    private final CourseMapper courseMapper;
    private final PaperQuestionMapper paperQuestionMapper;

    @Operation(summary = "获取待批改列表")
    @GetMapping("/tasks")
    public Result<PageResult<Map<String, Object>>> taskList(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {

        Page<GradingTask> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<GradingTask> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
            wrapper.eq(GradingTask::getStatus, status);
        } else {
            wrapper.in(GradingTask::getStatus, List.of("pending", "grading", "completed"));
        }
        wrapper.inSql(GradingTask::getQuestionId, "SELECT id FROM question WHERE type LIKE '%essay%' OR type LIKE '%code%'");
        // 按当前登录教师过滤
        Long teacherId = getCurrentUserId(request);
        if (teacherId != null) {
            wrapper.and(w -> w.eq(GradingTask::getGraderId, teacherId).or().isNull(GradingTask::getGraderId));
        }
        wrapper.orderByAsc(GradingTask::getCreateTime);
        Page<GradingTask> result = gradingTaskMapper.selectPage(pageParam, wrapper);

        // 收集需要查询的ID
        List<GradingTask> tasks = result.getRecords();
        Set<Long> examRecordIds = tasks.stream().map(GradingTask::getExamRecordId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> studentIds = tasks.stream().map(GradingTask::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> questionIds = tasks.stream().map(GradingTask::getQuestionId).filter(Objects::nonNull).collect(Collectors.toSet());

        // 查询关联数据
        Map<Long, ExamRecord> examRecordMap = new HashMap<>();
        if (!examRecordIds.isEmpty()) {
            List<ExamRecord> records = examRecordMapper.selectBatchIds(examRecordIds);
            examRecordMap = records.stream().collect(Collectors.toMap(ExamRecord::getId, r -> r));
        }

        Set<Long> examIds = examRecordMap.values().stream().map(ExamRecord::getExamId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Exam> examMap = new HashMap<>();
        if (!examIds.isEmpty()) {
            List<Exam> exams = examMapper.selectBatchIds(examIds);
            examMap = exams.stream().collect(Collectors.toMap(Exam::getId, e -> e));
        }

        Map<Long, SysUser> studentMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            List<SysUser> students = sysUserMapper.selectBatchIds(studentIds);
            studentMap = students.stream().collect(Collectors.toMap(SysUser::getId, s -> s));
        }

        Map<Long, Question> questionMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            List<Question> questions = questionMapper.selectBatchIds(questionIds);
            questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
        }

        // 构建返回结果
        List<Map<String, Object>> enrichedTasks = new ArrayList<>();
        for (GradingTask task : tasks) {
            Question question = questionMap.get(task.getQuestionId());
            if (question == null || !isManualQuestion(question.getType())) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", task.getId());
            item.put("taskId", task.getId());
            item.put("examRecordId", task.getExamRecordId());
            item.put("questionId", task.getQuestionId());
            item.put("studentId", task.getStudentId());
            item.put("status", task.getStatus());
            item.put("score", task.getScore());
            item.put("comment", task.getComment());
            item.put("createTime", task.getCreateTime());
            item.put("gradeTime", task.getGradeTime());

            // 关联学生信息
            SysUser student = studentMap.get(task.getStudentId());
            item.put("studentName", student != null ? student.getRealName() : ("学生" + task.getStudentId()));
            item.put("studentNumber", student != null ? student.getUsername() : "");

            // 关联考试信息
            ExamRecord examRecord = examRecordMap.get(task.getExamRecordId());
            if (examRecord != null) {
                Exam exam = examMap.get(examRecord.getExamId());
                item.put("examId", examRecord.getExamId());
                item.put("examName", exam != null ? exam.getName() : ("考试" + examRecord.getExamId()));
                if (exam != null) {
                    item.put("courseId", exam.getCourseId());
                    Course course = exam.getCourseId() != null ? courseMapper.selectById(exam.getCourseId()) : null;
                    item.put("courseName", course != null ? course.getName() : "");
                    if (exam.getExamPaperId() != null && task.getQuestionId() != null) {
                        PaperQuestion paperQuestion = paperQuestionMapper.selectOne(
                                new LambdaQueryWrapper<PaperQuestion>()
                                        .eq(PaperQuestion::getPaperId, exam.getExamPaperId())
                                        .eq(PaperQuestion::getQuestionId, task.getQuestionId())
                        );
                        item.put("questionNumber", paperQuestion != null ? paperQuestion.getSortOrder() : null);
                    }
                }
            } else {
                item.put("examId", null);
                item.put("examName", "");
                item.put("courseId", null);
                item.put("courseName", "");
            }

            // 关联题目信息
            if (question != null) {
                item.put("questionContent", question.getContent());
                item.put("questionSummary", question.getContent() != null && question.getContent().length() > 50
                        ? question.getContent().substring(0, 50) + "..." : question.getContent());
                item.put("questionType", question.getType());
                item.put("typeName", getTypeName(question.getType()));
                item.put("fullScore", question.getScore());
                item.put("correctAnswer", question.getAnswer());
                item.put("referenceAnswer", question.getAnalysis());
            } else {
                item.put("questionContent", "");
                item.put("questionSummary", "");
                item.put("questionType", "essay");
                item.put("typeName", "简答题");
                item.put("fullScore", 10);
                item.put("correctAnswer", "");
                item.put("referenceAnswer", "");
            }

            // 查询学生答案
            AnswerRecord answerRecord = answerRecordMapper.selectOne(
                    new LambdaQueryWrapper<AnswerRecord>()
                            .eq(AnswerRecord::getExamRecordId, task.getExamRecordId())
                            .eq(AnswerRecord::getQuestionId, task.getQuestionId())
            );
            item.put("studentAnswer", answerRecord != null ? answerRecord.getStudentAnswer() : "");

            enrichedTasks.add(item);
        }

        PageResult<Map<String, Object>> pageResult = new PageResult<>();
        pageResult.setList(enrichedTasks);
        pageResult.setPagination(new PageResult.Pagination(page, pageSize, enrichedTasks.size()));
        return Result.success(pageResult);
    }

    private boolean isManualQuestion(String type) {
        return type != null && (type.contains("essay") || type.contains("code"));
    }

    private String getTypeName(String type) {
        if (type == null) return "简答题";
        return switch (type) {
            case "single_choice", "single" -> "单选题";
            case "multi_choice", "multiple" -> "多选题";
            case "true_false", "judge" -> "判断题";
            case "fill_blank", "fill" -> "填空题";
            case "essay" -> "简答题";
            case "code" -> "编程题";
            default -> type;
        };
    }

    @Operation(summary = "批改题目")
    @PostMapping("/tasks/{taskId}/submit")
    public Result<Void> submitGrade(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {

        completeTask(taskId, new BigDecimal(body.get("score").toString()), (String) body.get("comment"), getCurrentUserId(request));
        return Result.success();
    }

    @Operation(summary = "批量批改")
    @PostMapping("/batch")
    public Result<Map<String, Integer>> batchGrade(@RequestBody List<Map<String, Object>> tasks, HttpServletRequest request) {
        int successCount = 0;
        Long graderId = getCurrentUserId(request);
        for (Map<String, Object> taskBody : tasks) {
            try {
                Long taskId = Long.valueOf(taskBody.get("taskId").toString());
                completeTask(taskId, new BigDecimal(taskBody.get("score").toString()), (String) taskBody.get("comment"), graderId);
                successCount++;
            } catch (Exception e) {
                // 记录错误，继续处理下一个
            }
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failedCount", tasks.size() - successCount);
        return Result.success(result);
    }

    @Operation(summary = "获取成绩复核申请列表")
    @GetMapping("/reviews")
    public Result<PageResult<Map<String, Object>>> reviewList(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {

        Page<GradeReview> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<GradeReview> wrapper = new LambdaQueryWrapper<>();
        if (examId != null) {
            wrapper.eq(GradeReview::getExamId, examId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(GradeReview::getStatus, status);
        }
        // 按当前登录教师过滤（通过Exam表关联）
        Long teacherId = getCurrentUserId(request);
        if (teacherId != null) {
            // 查询属于该教师的考试ID
            LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
            examWrapper.eq(Exam::getTeacherId, teacherId).select(Exam::getId);
            List<Exam> exams = examMapper.selectList(examWrapper);
            if (!exams.isEmpty()) {
                List<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toList());
                wrapper.in(GradeReview::getExamId, examIds);
            } else {
                // 没有属于该教师的考试，返回空结果
                return Result.success(PageResult.of(new ArrayList<>(), page, pageSize, 0));
            }
        }
        wrapper.orderByDesc(GradeReview::getCreateTime);
        Page<GradeReview> result = gradeReviewMapper.selectPage(pageParam, wrapper);

        // 收集需要查询的ID
        List<GradeReview> reviews = result.getRecords();
        Set<Long> studentIds = reviews.stream().map(GradeReview::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> examIds = reviews.stream().map(GradeReview::getExamId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> questionIds = reviews.stream().map(GradeReview::getQuestionId).filter(Objects::nonNull).collect(Collectors.toSet());

        // 查询关联数据
        Map<Long, SysUser> studentMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            List<SysUser> students = sysUserMapper.selectBatchIds(studentIds);
            studentMap = students.stream().collect(Collectors.toMap(SysUser::getId, s -> s));
        }

        Map<Long, Exam> examMap = new HashMap<>();
        if (!examIds.isEmpty()) {
            List<Exam> exams = examMapper.selectBatchIds(examIds);
            examMap = exams.stream().collect(Collectors.toMap(Exam::getId, e -> e));
        }

        Map<Long, Question> questionMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            List<Question> questions = questionMapper.selectBatchIds(questionIds);
            questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
        }

        // 构建返回结果
        List<Map<String, Object>> enrichedReviews = new ArrayList<>();
        for (GradeReview review : reviews) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", review.getId());
            item.put("studentId", review.getStudentId());
            item.put("examId", review.getExamId());
            item.put("questionId", review.getQuestionId());
            item.put("reason", review.getReason());
            item.put("studentComment", review.getStudentComment());
            item.put("originalScore", review.getOriginalScore() != null ? review.getOriginalScore().doubleValue() : 0);
            item.put("newScore", review.getNewScore() != null ? review.getNewScore().doubleValue() : null);
            item.put("teacherComment", review.getTeacherComment());
            item.put("status", review.getStatus() != null ? review.getStatus() : "pending");
            item.put("createTime", review.getCreateTime());

            // 关联学生信息
            SysUser student = studentMap.get(review.getStudentId());
            item.put("studentName", student != null ? student.getRealName() : ("学生" + review.getStudentId()));

            // 关联考试信息
            Exam exam = examMap.get(review.getExamId());
            item.put("examName", exam != null ? exam.getName() : "");

            // 关联题目信息
            Question question = questionMap.get(review.getQuestionId());
            if (question != null) {
                item.put("questionTitle", question.getContent() != null && question.getContent().length() > 50
                        ? question.getContent().substring(0, 50) + "..." : question.getContent());
                item.put("questionContent", question.getContent());
                item.put("referenceAnswer", question.getAnalysis());
                item.put("maxScore", question.getScore());
            } else {
                item.put("questionTitle", "");
                item.put("questionContent", "");
                item.put("referenceAnswer", "");
                item.put("maxScore", 100);
            }

            // 查询学生答案
            AnswerRecord answerRecord = answerRecordMapper.selectOne(
                    new LambdaQueryWrapper<AnswerRecord>()
                            .eq(AnswerRecord::getExamRecordId, getExamRecordIdByGradeId(review.getGradeId()))
                            .eq(AnswerRecord::getQuestionId, review.getQuestionId())
            );
            item.put("studentAnswer", answerRecord != null ? answerRecord.getStudentAnswer() : "");
            item.put("questionScore", answerRecord != null && answerRecord.getScore() != null ? answerRecord.getScore() : null);

            enrichedReviews.add(item);
        }

        return Result.success(PageResult.of(enrichedReviews, page, pageSize, result.getTotal()));
    }

    @Operation(summary = "处理成绩复核")
    @PostMapping("/reviews/{reviewId}/handle")
    public Result<Void> handleReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {

        GradeReview review = new GradeReview();
        review.setId(reviewId);
        // 前端传 action 字段（approve/reject），转换为 status
        String action = (String) body.get("action");
        if ("approve".equals(action)) {
            review.setStatus("approved");
        } else if ("reject".equals(action)) {
            review.setStatus("rejected");
        } else {
            // 兼容直接传 status 的情况
            review.setStatus((String) body.get("status"));
        }
        review.setTeacherComment((String) body.get("teacherComment"));
        Long handlerId = getCurrentUserId(request);
        review.setHandlerId(handlerId != null ? handlerId : 1L);
        review.setHandleTime(LocalDateTime.now());
        if (body.get("newScore") != null) {
            review.setNewScore(new BigDecimal(body.get("newScore").toString()));
        }
        gradeReviewMapper.updateById(review);
        applyReviewScore(reviewId, review.getStatus(), review.getNewScore());
        return Result.success();
    }

    // ========== 兼容前端路径的方法 ==========

    @Operation(summary = "更新批改成绩（兼容前端 PUT /teacher/grading/tasks/{taskId}）")
    @PutMapping("/tasks/{taskId}")
    public Result<Void> updateGrade(@PathVariable Long taskId, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        completeTask(taskId, new BigDecimal(body.get("score").toString()), (String) body.get("comment"), getCurrentUserId(request));
        return Result.success();
    }

    private void completeTask(Long taskId, BigDecimal score, String comment, Long graderId) {
        GradingTask existingTask = gradingTaskMapper.selectById(taskId);
        if (existingTask == null) {
            return;
        }

        GradingTask task = new GradingTask();
        task.setId(taskId);
        task.setScore(score);
        task.setComment(comment);
        task.setStatus("completed");
        task.setGraderId(graderId != null ? graderId : 1L);
        task.setGradeTime(LocalDateTime.now());
        gradingTaskMapper.updateById(task);

        AnswerRecord answerRecord = answerRecordMapper.selectOne(
                new LambdaQueryWrapper<AnswerRecord>()
                        .eq(AnswerRecord::getExamRecordId, existingTask.getExamRecordId())
                        .eq(AnswerRecord::getQuestionId, existingTask.getQuestionId())
                        .last("LIMIT 1")
        );
        if (answerRecord != null) {
            answerRecord.setScore(score.doubleValue());
            answerRecord.setIsCorrect(score.compareTo(BigDecimal.ZERO) > 0);
            answerRecord.setLastModifyTime(LocalDateTime.now());
            answerRecordMapper.updateById(answerRecord);
        }

        recalculateRecordAndGrade(existingTask.getExamRecordId());
    }

    private void recalculateRecordAndGrade(Long examRecordId) {
        ExamRecord record = examRecordId != null ? examRecordMapper.selectById(examRecordId) : null;
        if (record == null) {
            return;
        }
        List<AnswerRecord> answers = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, examRecordId)
        );

        double objectiveScore = 0.0;
        double subjectiveScore = 0.0;
        for (AnswerRecord answer : answers) {
            Question question = answer.getQuestionId() != null ? questionMapper.selectById(answer.getQuestionId()) : null;
            if (question == null || answer.getScore() == null) {
                continue;
            }
            String type = question.getType();
            boolean subjective = type != null && (type.contains("essay") || type.contains("code"));
            if (subjective) {
                subjectiveScore += answer.getScore();
            } else {
                objectiveScore += answer.getScore();
            }
        }

        long pendingCount = gradingTaskMapper.selectCount(
                new LambdaQueryWrapper<GradingTask>()
                        .eq(GradingTask::getExamRecordId, examRecordId)
                        .ne(GradingTask::getStatus, "completed")
        );

        record.setObjectiveScore(objectiveScore);
        record.setSubjectiveScore(subjectiveScore);
        record.setTotalScore(objectiveScore + subjectiveScore);
        record.setAnsweredCount(answers.size());
        examRecordMapper.updateById(record);

        Grade grade = gradeMapper.selectOne(
                new LambdaQueryWrapper<Grade>().eq(Grade::getExamRecordId, examRecordId).last("LIMIT 1")
        );
        if (grade == null) {
            grade = new Grade();
            grade.setExamRecordId(record.getId());
            grade.setExamId(record.getExamId());
            grade.setStudentId(record.getStudentId());
        }
        grade.setObjectiveScore(BigDecimal.valueOf(objectiveScore));
        grade.setSubjectiveScore(BigDecimal.valueOf(subjectiveScore));
        grade.setTotalScore(BigDecimal.valueOf(objectiveScore + subjectiveScore));
        grade.setStatus(pendingCount > 0 ? "pending" : "published");
        grade.setPublishTime(LocalDateTime.now());
        if (grade.getId() == null) {
            gradeMapper.insert(grade);
        } else {
            gradeMapper.updateById(grade);
        }
    }

    @Operation(summary = "批改历史记录（兼容前端 GET /teacher/grading/history）")
    @GetMapping("/history")
    public Result<PageResult<Map<String, Object>>> history(
            @RequestParam(required = false) Long examId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        Page<GradingTask> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<GradingTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GradingTask::getStatus, "completed");
        // 按当前登录教师过滤
        Long teacherId = getCurrentUserId(request);
        if (teacherId != null) {
            wrapper.eq(GradingTask::getGraderId, teacherId);
        }
        wrapper.orderByDesc(GradingTask::getGradeTime);
        Page<GradingTask> result = gradingTaskMapper.selectPage(pageParam, wrapper);
        List<Map<String, Object>> enriched = enrichGradingTasks(result.getRecords());
        return Result.success(PageResult.of(enriched, page, pageSize, result.getTotal()));
    }

    private List<Map<String, Object>> enrichGradingTasks(List<GradingTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }
        Set<Long> examRecordIds = tasks.stream().map(GradingTask::getExamRecordId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> studentIds = tasks.stream().map(GradingTask::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> questionIds = tasks.stream().map(GradingTask::getQuestionId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> graderIds = tasks.stream().map(GradingTask::getGraderId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, ExamRecord> examRecordMap = examRecordIds.isEmpty()
                ? new HashMap<>()
                : examRecordMapper.selectBatchIds(examRecordIds).stream().collect(Collectors.toMap(ExamRecord::getId, r -> r));
        Set<Long> examIds = examRecordMap.values().stream().map(ExamRecord::getExamId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Exam> examMap = examIds.isEmpty()
                ? new HashMap<>()
                : examMapper.selectBatchIds(examIds).stream().collect(Collectors.toMap(Exam::getId, e -> e));
        Map<Long, SysUser> studentMap = studentIds.isEmpty()
                ? new HashMap<>()
                : sysUserMapper.selectBatchIds(studentIds).stream().collect(Collectors.toMap(SysUser::getId, s -> s));
        Map<Long, SysUser> graderMap = graderIds.isEmpty()
                ? new HashMap<>()
                : sysUserMapper.selectBatchIds(graderIds).stream().collect(Collectors.toMap(SysUser::getId, s -> s));
        Map<Long, Question> questionMap = questionIds.isEmpty()
                ? new HashMap<>()
                : questionMapper.selectBatchIds(questionIds).stream().collect(Collectors.toMap(Question::getId, q -> q));

        List<Map<String, Object>> enriched = new ArrayList<>();
        for (GradingTask task : tasks) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", task.getId());
            item.put("taskId", task.getId());
            item.put("examRecordId", task.getExamRecordId());
            item.put("questionId", task.getQuestionId());
            item.put("studentId", task.getStudentId());
            item.put("status", task.getStatus());
            item.put("score", task.getScore());
            item.put("comment", task.getComment());
            item.put("createTime", task.getCreateTime());
            item.put("gradeTime", task.getGradeTime());

            SysUser student = studentMap.get(task.getStudentId());
            item.put("studentName", student != null ? student.getRealName() : ("学生" + task.getStudentId()));

            SysUser grader = graderMap.get(task.getGraderId());
            item.put("teacherName", grader != null ? grader.getRealName() : "");

            ExamRecord record = examRecordMap.get(task.getExamRecordId());
            Exam exam = record != null ? examMap.get(record.getExamId()) : null;
            item.put("examId", record != null ? record.getExamId() : null);
            item.put("examName", exam != null ? exam.getName() : "");

            Question question = questionMap.get(task.getQuestionId());
            item.put("questionType", question != null ? question.getType() : "essay");
            item.put("typeName", question != null ? getTypeName(question.getType()) : "主观题");
            item.put("fullScore", question != null ? question.getScore() : BigDecimal.TEN);
            item.put("questionContent", question != null ? question.getContent() : "");
            enriched.add(item);
        }
        return enriched;
    }

    @Operation(summary = "复核申请列表（兼容前端 GET /teacher/reviews）")
    @GetMapping("/reviewsCompat")
    public Result<PageResult<Map<String, Object>>> reviewsCompat(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        return reviewList(examId, status, page, pageSize, request);
    }

    @Operation(summary = "处理复核申请（兼容前端 PUT /teacher/reviews/{reviewId}）")
    @PutMapping("/reviewsCompat/{reviewId}")
    public Result<Void> handleReviewCompat(
            @PathVariable Long reviewId,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        GradeReview review = new GradeReview();
        review.setId(reviewId);
        review.setStatus((String) body.getOrDefault("action", "approved"));
        review.setTeacherComment((String) body.get("teacherComment"));
        if (body.get("newScore") != null) review.setNewScore(new BigDecimal(body.get("newScore").toString()));
        Long handlerId = getCurrentUserId(request);
        review.setHandlerId(handlerId != null ? handlerId : 1L);
        review.setHandleTime(LocalDateTime.now());
        gradeReviewMapper.updateById(review);
        applyReviewScore(reviewId, review.getStatus(), review.getNewScore());
        return Result.success();
    }

    private void applyReviewScore(Long reviewId, String status, BigDecimal newScore) {
        if (!"approved".equals(status) || newScore == null) {
            return;
        }
        GradeReview fullReview = gradeReviewMapper.selectById(reviewId);
        if (fullReview == null || fullReview.getGradeId() == null) {
            return;
        }
        Grade grade = gradeMapper.selectById(fullReview.getGradeId());
        if (grade == null) {
            return;
        }

        grade.setStatus("reviewed");
        grade.setPublishTime(LocalDateTime.now());
        if (fullReview.getQuestionId() != null) {
            AnswerRecord answerRecord = grade.getExamRecordId() != null ? answerRecordMapper.selectOne(
                    new LambdaQueryWrapper<AnswerRecord>()
                            .eq(AnswerRecord::getExamRecordId, grade.getExamRecordId())
                            .eq(AnswerRecord::getQuestionId, fullReview.getQuestionId())
                            .last("LIMIT 1")
            ) : null;
            BigDecimal oldQuestionScore = answerRecord != null && answerRecord.getScore() != null
                    ? BigDecimal.valueOf(answerRecord.getScore()) : BigDecimal.ZERO;
            BigDecimal delta = newScore.subtract(oldQuestionScore);
            BigDecimal oldTotal = grade.getTotalScore() != null ? grade.getTotalScore() : BigDecimal.ZERO;
            BigDecimal oldSubjective = grade.getSubjectiveScore() != null ? grade.getSubjectiveScore() : BigDecimal.ZERO;
            grade.setTotalScore(oldTotal.add(delta));
            grade.setSubjectiveScore(oldSubjective.add(delta));
            if (answerRecord != null) {
                answerRecord.setScore(newScore.doubleValue());
                answerRecord.setIsCorrect(newScore.compareTo(BigDecimal.ZERO) > 0);
                answerRecord.setLastModifyTime(LocalDateTime.now());
                answerRecordMapper.updateById(answerRecord);
            }
        } else {
            grade.setTotalScore(newScore);
        }
        gradeMapper.updateById(grade);

        ExamRecord record = grade.getExamRecordId() != null ? examRecordMapper.selectById(grade.getExamRecordId()) : null;
        if (record != null) {
            if (fullReview.getQuestionId() != null) {
                record.setTotalScore(grade.getTotalScore().doubleValue());
                record.setSubjectiveScore(grade.getSubjectiveScore() != null ? grade.getSubjectiveScore().doubleValue() : 0.0);
            } else {
                record.setTotalScore(newScore.doubleValue());
            }
            examRecordMapper.updateById(record);
        }
    }

    private Long getExamRecordIdByGradeId(Long gradeId) {
        if (gradeId == null) return null;
        Grade grade = gradeMapper.selectById(gradeId);
        return grade != null ? grade.getExamRecordId() : null;
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            try {
                return Long.parseLong(userId);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
