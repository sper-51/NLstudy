package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.entity.ExamRecord;
import com.nl.nlstudy.entity.AnswerRecord;
import com.nl.nlstudy.entity.GradingTask;
import com.nl.nlstudy.entity.Grade;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.entity.PaperQuestion;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.mapper.ExamMapper;
import com.nl.nlstudy.mapper.ExamRecordMapper;
import com.nl.nlstudy.mapper.PaperQuestionMapper;
import com.nl.nlstudy.mapper.SysUserMapper;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.AnswerRecordMapper;
import com.nl.nlstudy.mapper.GradingTaskMapper;
import com.nl.nlstudy.mapper.GradeMapper;
import com.nl.nlstudy.mapper.QuestionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "教师端-考试安排")
@RestController
@RequestMapping("/teacher/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final CourseMapper courseMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final GradingTaskMapper gradingTaskMapper;
    private final GradeMapper gradeMapper;
    private final QuestionMapper questionMapper;

    @Operation(summary = "分页查询教师考试")
    @GetMapping
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        Page<Exam> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        if (teacherId != null) {
            wrapper.eq(Exam::getTeacherId, teacherId);
        }
        if (courseId != null) {
            wrapper.eq(Exam::getCourseId, courseId);
        }
        if (status != null && !status.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            switch (status) {
                case "ongoing" -> wrapper.eq(Exam::getStatus, "published")
                        .le(Exam::getStartTime, now)
                        .ge(Exam::getEndTime, now);
                case "finished" -> wrapper.lt(Exam::getEndTime, now);
                case "draft" -> wrapper.eq(Exam::getStatus, "draft");
                default -> wrapper.eq(Exam::getStatus, status);
            }
        }
        wrapper.orderByDesc(Exam::getStartTime);
        Page<Exam> result = examMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> records = result.getRecords().stream().map(exam -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", exam.getId());
            item.put("name", exam.getName());
            item.put("examName", exam.getName());
            item.put("examPaperId", exam.getExamPaperId());
            item.put("courseId", exam.getCourseId());
            item.put("teacherId", exam.getTeacherId());
            item.put("startTime", exam.getStartTime());
            item.put("endTime", exam.getEndTime());
            item.put("duration", exam.getDuration());
            item.put("totalScore", exam.getTotalScore());
            item.put("passScore", exam.getPassScore());
            item.put("allowTimes", exam.getAllowTimes());
            item.put("examType", exam.getExamType());
            item.put("status", exam.getStatus());

            LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(ExamRecord::getExamId, exam.getId());
            List<ExamRecord> examRecords = examRecordMapper.selectList(recordWrapper);
            int participantCount = examRecords.size();
            int submitCount = (int) examRecords.stream()
                    .filter(r -> "submitted".equals(r.getStatus()) || "auto_submitted".equals(r.getStatus()))
                    .count();
            item.put("studentCount", participantCount);
            item.put("participantCount", participantCount);
            item.put("submitCount", submitCount);
            item.put("submittedCount", submitCount);
            item.put("submissionRate", participantCount > 0 ? Math.round(submitCount * 10000.0 / participantCount) / 100.0 : 0.0);
            List<Grade> grades = gradeMapper.selectList(new LambdaQueryWrapper<Grade>().eq(Grade::getExamId, exam.getId()));
            double avgScore = grades.stream()
                    .map(Grade::getTotalScore)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .orElse(0.0);
            double maxScore = grades.stream()
                    .map(Grade::getTotalScore)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .max()
                    .orElse(0.0);
            double minScore = grades.stream()
                    .map(Grade::getTotalScore)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .min()
                    .orElse(0.0);
            item.put("avgScore", Math.round(avgScore * 10) / 10.0);
            item.put("maxScore", Math.round(maxScore * 10) / 10.0);
            item.put("minScore", Math.round(minScore * 10) / 10.0);

            int questionCount = 0;
            if (exam.getExamPaperId() != null) {
                LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
                pqWrapper.eq(PaperQuestion::getPaperId, exam.getExamPaperId());
                questionCount = Math.toIntExact(paperQuestionMapper.selectCount(pqWrapper));
            }
            item.put("questionCount", questionCount);
            return item;
        }).collect(Collectors.toList());

        return Result.success(PageResult.of(records, page, pageSize, result.getTotal()));
    }

    @Operation(summary = "发布考试")
    @PostMapping
    public Result<Map<String, Object>> publish(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Exam exam = new Exam();
        exam.setName((String) body.get("name"));
        exam.setExamPaperId(Long.valueOf(body.get("examPaperId").toString()));
        exam.setCourseId(Long.valueOf(body.get("courseId").toString()));
        Long teacherId = getCurrentUserId(request);
        exam.setTeacherId(teacherId != null ? teacherId : 1L);
        exam.setStartTime(LocalDateTime.parse((CharSequence) body.get("startTime")));
        exam.setEndTime(LocalDateTime.parse((CharSequence) body.get("endTime")));
        exam.setDuration(Integer.valueOf(body.get("duration").toString()));
        exam.setTotalScore(new java.math.BigDecimal(body.get("totalScore").toString()));
        exam.setPassScore(body.get("passScore") != null ? new java.math.BigDecimal(body.get("passScore").toString()) : new java.math.BigDecimal("60"));
        exam.setAllowTimes(Integer.valueOf(body.getOrDefault("allowTimes", "1").toString()));
        exam.setIsRandomOrder(Integer.valueOf(body.getOrDefault("isRandomOrder", "1").toString()));
        exam.setIsRandomOptions(Integer.valueOf(body.getOrDefault("isRandomOptions", "1").toString()));
        exam.setExamType((String) body.getOrDefault("examType", "formal"));
        exam.setStatus("published");
        exam.setStudentCount(Integer.valueOf(body.getOrDefault("studentCount", "0").toString()));

        examMapper.insert(exam);
        return Result.success(Map.of("examId", exam.getId(), "studentCount", exam.getStudentCount()));
    }

    @Operation(summary = "取消考试")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Exam exam = new Exam();
        exam.setId(id);
        exam.setStatus("cancelled");
        examMapper.updateById(exam);
        return Result.success();
    }

    @Operation(summary = "强制交卷")
    @PostMapping("/{examId}/forceSubmit/{studentId}")
    public Result<Void> forceSubmit(@PathVariable Long examId, @PathVariable Long studentId) {
        // 查询该考试下指定学生的未提交记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
               .in(ExamRecord::getStatus, "ongoing", "pending");
        if (studentId != null && studentId > 0) {
            wrapper.eq(ExamRecord::getStudentId, studentId);
        }
        List<ExamRecord> records = examRecordMapper.selectList(wrapper);

        if (records != null && !records.isEmpty()) {
            for (ExamRecord record : records) {
                record.setStatus("auto_submitted");
                record.setSubmitTime(LocalDateTime.now());
                if (record.getStartTime() != null) {
                    record.setDurationSeconds(java.time.Duration.between(record.getStartTime(), record.getSubmitTime()).getSeconds());
                }
                settleRecord(examId, record);
                examRecordMapper.updateById(record);
            }
        }

        return Result.success();
    }

    @Operation(summary = "编辑考试")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Exam exam = new Exam();
        exam.setId(id);
        if (body.containsKey("name")) exam.setName((String) body.get("name"));
        if (body.containsKey("startTime")) exam.setStartTime(LocalDateTime.parse((CharSequence) body.get("startTime")));
        if (body.containsKey("endTime")) exam.setEndTime(LocalDateTime.parse((CharSequence) body.get("endTime")));
        if (body.containsKey("duration")) exam.setDuration(Integer.valueOf(body.get("duration").toString()));
        if (body.containsKey("totalScore")) exam.setTotalScore(new BigDecimal(body.get("totalScore").toString()));
        if (body.containsKey("passScore")) exam.setPassScore(new BigDecimal(body.getOrDefault("passScore", "60").toString()));
        examMapper.updateById(exam);
        return Result.success();
    }

    @Operation(summary = "获取考试详情")
    @GetMapping("/{examId}/detail")
    public Result<Map<String, Object>> getExamDetail(@PathVariable Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", exam.getId());
        result.put("name", exam.getName());
        result.put("examPaperId", exam.getExamPaperId());
        result.put("courseId", exam.getCourseId());
        result.put("teacherId", exam.getTeacherId());
        result.put("semesterId", exam.getSemesterId());
        result.put("startTime", exam.getStartTime());
        result.put("endTime", exam.getEndTime());
        result.put("duration", exam.getDuration());
        result.put("totalScore", exam.getTotalScore());
        result.put("passScore", exam.getPassScore());
        result.put("allowTimes", exam.getAllowTimes());
        result.put("isRandomOrder", exam.getIsRandomOrder());
        result.put("isRandomOptions", exam.getIsRandomOptions());
        result.put("examType", exam.getExamType());
        result.put("status", exam.getStatus());
        // 动态统计参加人数与提交人数，避免依赖历史脏数据

        // 获取课程名称
        if (exam.getCourseId() != null) {
            var course = courseMapper.selectById(exam.getCourseId());
            result.put("courseName", course != null ? course.getName() : "");
        } else {
            result.put("courseName", "");
        }
        SysUser teacher = sysUserMapper.selectById(exam.getTeacherId());
        result.put("teacherName", teacher != null ? teacher.getRealName() : "");

        // 获取参加考试的学生列表
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getExamId, examId);
        List<ExamRecord> records = examRecordMapper.selectList(recordWrapper);

        int studentCount = records.size();
        int submitCount = (int) records.stream().filter(r -> "submitted".equals(r.getStatus()) || "auto_submitted".equals(r.getStatus())).count();
        result.put("studentCount", studentCount);
        result.put("submitCount", submitCount);

        int totalQuestions = 0;
        if (exam.getExamPaperId() != null) {
            LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
            pqWrapper.eq(PaperQuestion::getPaperId, exam.getExamPaperId());
            totalQuestions = Math.toIntExact(paperQuestionMapper.selectCount(pqWrapper));
        }

        List<Map<String, Object>> studentList = new ArrayList<>();
        for (ExamRecord record : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("studentId", record.getStudentId());
            
            SysUser student = sysUserMapper.selectById(record.getStudentId());
            item.put("studentName", student != null ? student.getRealName() : "");
            
            item.put("status", mapExamStatus(record.getStatus()));
            item.put("submitTime", record.getSubmitTime());
            item.put("totalScore", record.getTotalScore());
            studentList.add(item);
        }
        result.put("students", studentList);

        return Result.success(result);
    }

    @Operation(summary = "获取监控数据")
    @GetMapping("/{examId}/monitor")
    public Result<List<Map<String, Object>>> getMonitorData(@PathVariable Long examId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId);
        List<ExamRecord> records = examRecordMapper.selectList(wrapper);

        if (records == null || records.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 获取学生信息
        Set<Long> studentIds = records.stream()
                .map(ExamRecord::getStudentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, SysUser> userMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(studentIds);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Exam exam = examMapper.selectById(examId);
        int totalQuestions = 0;
        if (exam != null && exam.getExamPaperId() != null) {
            LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
            pqWrapper.eq(PaperQuestion::getPaperId, exam.getExamPaperId());
            totalQuestions = Math.toIntExact(paperQuestionMapper.selectCount(pqWrapper));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ExamRecord record : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", record.getId());
            item.put("recordId", record.getId());
            item.put("examRecordId", record.getId());
            item.put("studentId", record.getStudentId());
            item.put("studentName", record.getStudentId() != null && userMap.containsKey(record.getStudentId())
                    ? userMap.get(record.getStudentId()).getRealName() : "学生" + record.getStudentId());
            item.put("status", mapExamStatus(record.getStatus()));
            item.put("startTime", record.getStartTime() != null ? record.getStartTime().format(formatter) : null);
            item.put("answeredCount", record.getAnsweredCount() != null ? record.getAnsweredCount() : 0);
            item.put("totalQuestions", record.getTotalQuestions() != null && record.getTotalQuestions() > 0 ? record.getTotalQuestions() : totalQuestions);
            item.put("switchScreenCount", record.getSwitchScreenCount() != null ? record.getSwitchScreenCount() : 0);
            item.put("networkInterruptCount", record.getNetworkInterruptCount() != null ? record.getNetworkInterruptCount() : 0);
            item.put("lastActiveTime", record.getSubmitTime() != null ? record.getSubmitTime().format(formatter) : (record.getStartTime() != null ? record.getStartTime().format(formatter) : null));
            item.put("totalScore", record.getTotalScore());
            result.add(item);
        }

        return Result.success(result);
    }

    @Operation(summary = "结束考试并立即结算")
    @PostMapping("/{examId}/finish")
    public Result<Map<String, Object>> finishExam(@PathVariable Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }

        List<ExamRecord> records = examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getExamId, examId)
        );

        int settledCount = 0;
        int gradingTaskCount = 0;
        for (ExamRecord record : records) {
            if (!"submitted".equals(record.getStatus()) && !"auto_submitted".equals(record.getStatus())) {
                record.setStatus("auto_submitted");
                record.setSubmitTime(LocalDateTime.now());
                if (record.getStartTime() != null) {
                    record.setDurationSeconds(java.time.Duration.between(record.getStartTime(), record.getSubmitTime()).getSeconds());
                }
            }
            gradingTaskCount += settleRecord(examId, record);
            examRecordMapper.updateById(record);
            settledCount++;
        }

        exam.setStatus("finished");
        exam.setEndTime(LocalDateTime.now());
        examMapper.updateById(exam);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("settledCount", settledCount);
        result.put("gradingTaskCount", gradingTaskCount);
        result.put("status", "finished");
        return Result.success(result);
    }

    private String mapExamStatus(String status) {
        if (status == null) return "pending";
        return switch (status) {
            case "ongoing" -> "ongoing";
            case "submitted", "auto_submitted" -> "submitted";
            default -> "pending";
        };
    }

    private int settleRecord(Long examId, ExamRecord record) {
        List<AnswerRecord> answers = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, record.getId())
        );

        double objectiveScore = 0.0;
        double subjectiveScore = 0.0;
        int createdTasks = 0;

        for (AnswerRecord answer : answers) {
            Question question = answer.getQuestionId() != null ? questionMapper.selectById(answer.getQuestionId()) : null;
            if (question == null) {
                continue;
            }
            String type = question.getType();
            boolean subjective = type != null && (type.contains("essay") || type.contains("code"));
            if (subjective) {
                if (answer.getScore() != null) {
                    subjectiveScore += answer.getScore();
                }
                LambdaQueryWrapper<GradingTask> taskWrapper = new LambdaQueryWrapper<>();
                taskWrapper.eq(GradingTask::getExamRecordId, record.getId())
                        .eq(GradingTask::getQuestionId, answer.getQuestionId());
                if (gradingTaskMapper.selectCount(taskWrapper) == 0) {
                    GradingTask task = new GradingTask();
                    task.setExamRecordId(record.getId());
                    task.setQuestionId(answer.getQuestionId());
                    task.setStudentId(record.getStudentId());
                    task.setGraderId(null);
                    task.setStatus("pending");
                    task.setScore(null);
                    task.setComment(null);
                    task.setCreateTime(LocalDateTime.now());
                    gradingTaskMapper.insert(task);
                    createdTasks++;
                }
            } else {
                if (answer.getScore() != null) {
                    objectiveScore += answer.getScore();
                } else if (Boolean.TRUE.equals(answer.getIsCorrect()) && question.getScore() != null) {
                    objectiveScore += question.getScore().doubleValue();
                }
            }
        }

        record.setObjectiveScore(objectiveScore);
        record.setSubjectiveScore(subjectiveScore);
        record.setTotalScore(objectiveScore + subjectiveScore);
        record.setAnsweredCount(answers.size());

        LambdaQueryWrapper<Grade> gradeWrapper = new LambdaQueryWrapper<>();
        gradeWrapper.eq(Grade::getExamRecordId, record.getId());
        Grade grade = gradeMapper.selectOne(gradeWrapper);
        if (grade == null) {
            grade = new Grade();
            grade.setExamRecordId(record.getId());
            grade.setExamId(examId);
            grade.setStudentId(record.getStudentId());
            grade.setStatus(hasPendingGradingTask(record.getId()) ? "pending" : "published");
            grade.setPublishTime(LocalDateTime.now());
            grade.setObjectiveScore(BigDecimal.valueOf(objectiveScore));
            grade.setSubjectiveScore(BigDecimal.valueOf(subjectiveScore));
            grade.setTotalScore(BigDecimal.valueOf(objectiveScore + subjectiveScore));
            gradeMapper.insert(grade);
        } else {
            grade.setObjectiveScore(BigDecimal.valueOf(objectiveScore));
            grade.setSubjectiveScore(BigDecimal.valueOf(subjectiveScore));
            grade.setTotalScore(BigDecimal.valueOf(objectiveScore + subjectiveScore));
            grade.setStatus(hasPendingGradingTask(record.getId()) ? "pending" : "published");
            grade.setPublishTime(LocalDateTime.now());
            gradeMapper.updateById(grade);
        }
        return createdTasks;
    }

    private boolean hasPendingGradingTask(Long examRecordId) {
        return gradingTaskMapper.selectCount(
                new LambdaQueryWrapper<GradingTask>()
                        .eq(GradingTask::getExamRecordId, examRecordId)
                        .ne(GradingTask::getStatus, "completed")
        ) > 0;
    }

    @Operation(summary = "安排补考")
    @PostMapping("/{examId}/makeup")
    public Result<Void> scheduleMakeup(
            @PathVariable Long examId,
            @RequestBody Map<String, Object> body) {
        // 获取原考试信息
        Exam originalExam = examMapper.selectById(examId);
        if (originalExam == null) {
            return Result.error("原考试不存在");
        }

        // 创建补考记录
        Exam makeupExam = new Exam();
        makeupExam.setName(originalExam.getName() + "(补考)");
        makeupExam.setExamPaperId(originalExam.getExamPaperId());
        makeupExam.setCourseId(originalExam.getCourseId());
        makeupExam.setTeacherId(originalExam.getTeacherId());
        makeupExam.setSemesterId(originalExam.getSemesterId());
        makeupExam.setStartTime(LocalDateTime.parse((CharSequence) body.getOrDefault("startTime", "")));
        makeupExam.setEndTime(LocalDateTime.parse((CharSequence) body.getOrDefault("endTime", "")));
        makeupExam.setDuration(Integer.valueOf(body.getOrDefault("duration", "60").toString()));
        makeupExam.setTotalScore(originalExam.getTotalScore());
        makeupExam.setPassScore(originalExam.getPassScore());
        makeupExam.setAllowTimes(1);
        makeupExam.setIsRandomOrder(0);
        makeupExam.setIsRandomOptions(0);
        makeupExam.setExamType("makeup");
        makeupExam.setStatus("published");

        examMapper.insert(makeupExam);
        return Result.success();
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
