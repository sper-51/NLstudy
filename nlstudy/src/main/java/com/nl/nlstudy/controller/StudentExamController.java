package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.*;
import com.nl.nlstudy.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "学生端-考试模块")
@RestController
@RequestMapping("/student/exams")
@RequiredArgsConstructor
public class StudentExamController {

    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final CourseMapper courseMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final GradeMapper gradeMapper;
    private final GradingTaskMapper gradingTaskMapper;

    @Operation(summary = "获取学生考试列表")
    @GetMapping
    public Result<List<Map<String, Object>>> getExamList(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        final Long currentStudentId = studentId;

        LocalDateTime now = LocalDateTime.now();
        
        // 查询所有已发布的考试
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
        examWrapper.eq(Exam::getStatus, "published");
        List<Exam> exams = examMapper.selectList(examWrapper);

        List<Map<String, Object>> result = exams.stream().map(exam -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", exam.getId());
            map.put("name", exam.getName());
            map.put("courseId", exam.getCourseId());
            map.put("startTime", exam.getStartTime());
            map.put("endTime", exam.getEndTime());
            map.put("duration", exam.getDuration());
            map.put("totalScore", exam.getTotalScore());
            map.put("passScore", exam.getPassScore());
            map.put("examType", exam.getExamType());
            map.put("status", exam.getStatus());

            // 获取课程名称
            Course course = courseMapper.selectById(exam.getCourseId());
            if (course != null) {
                map.put("courseName", course.getName());
            }

            // 判断考试状态
            String examStatus;
            if (now.isBefore(exam.getStartTime())) {
                examStatus = "not_started";
            } else if (now.isAfter(exam.getEndTime())) {
                examStatus = "ended";
            } else {
                examStatus = "ongoing";
            }
            map.put("examStatus", examStatus);

            // 检查是否已有考试记录
            LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(ExamRecord::getExamId, exam.getId())
                    .eq(ExamRecord::getStudentId, currentStudentId);
            ExamRecord record = examRecordMapper.selectOne(recordWrapper);
            
            if (record != null) {
                map.put("recordId", record.getId());
                map.put("submitStatus", mapExamStatus(record.getStatus()));
                map.put("totalScore", record.getTotalScore());
            } else {
                map.put("recordId", null);
                map.put("submitStatus", "not_taken");
            }

            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "获取考试详情")
    @GetMapping("/{examId}")
    public Result<Map<String, Object>> getExamDetail(
            @PathVariable Long examId,
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }

        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", exam.getId());
        result.put("name", exam.getName());
        result.put("courseId", exam.getCourseId());
        result.put("startTime", exam.getStartTime());
        result.put("endTime", exam.getEndTime());
        result.put("duration", exam.getDuration());
        result.put("totalScore", exam.getTotalScore());
        result.put("passScore", exam.getPassScore());
        result.put("allowTimes", exam.getAllowTimes());
        result.put("isRandomOrder", exam.getIsRandomOrder());
        result.put("isRandomOptions", exam.getIsRandomOptions());
        result.put("examType", exam.getExamType());

        // 获取课程名称
        Course course = courseMapper.selectById(exam.getCourseId());
        if (course != null) {
            result.put("courseName", course.getName());
        }

        // 获取考试记录
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStudentId, studentId);
        ExamRecord record = examRecordMapper.selectOne(recordWrapper);
        
        if (record != null) {
            result.put("recordId", record.getId());
            result.put("status", record.getStatus());
            result.put("startTime", record.getStartTime());
            result.put("submitTime", record.getSubmitTime());
            result.put("totalScore", record.getTotalScore());
        }

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

    @Operation(summary = "获取待参加考试列表")
    @GetMapping("/pending")
    public Result<List<Map<String, Object>>> pendingExams(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }
        final Long currentStudentId = studentId;

        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getStatus, "published")
                .le(Exam::getStartTime, now)
                .ge(Exam::getEndTime, now);
        List<Exam> exams = examMapper.selectList(wrapper);

        List<Map<String, Object>> result = exams.stream().map(exam -> {
            Map<String, Object> map = new HashMap<>();
            map.put("examId", exam.getId());
            map.put("examName", exam.getName());
            map.put("courseId", exam.getCourseId());
            map.put("startTime", exam.getStartTime());
            map.put("endTime", exam.getEndTime());
            map.put("duration", exam.getDuration());
            map.put("totalScore", exam.getTotalScore());
            map.put("examType", exam.getExamType());

            // 获取课程名称
            Course course = courseMapper.selectById(exam.getCourseId());
            if (course != null) {
                map.put("courseName", course.getName());
            }

            // 检查是否已参加：待考列表只保留未参加或进行中可继续的记录，已提交进入 history
            LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(ExamRecord::getExamId, exam.getId())
                    .eq(ExamRecord::getStudentId, currentStudentId);
            ExamRecord record = examRecordMapper.selectOne(recordWrapper);
            if (record != null && !"ongoing".equals(record.getStatus())) {
                return null;
            }
            map.put("status", record != null ? "ongoing" : "pending");
            map.put("submitStatus", record != null ? mapExamStatus(record.getStatus()) : "not_taken");
            map.put("allowTimes", exam.getAllowTimes());

            return map;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "开始考试")
    @PostMapping("/{examId}/start")
    public Result<Map<String, Object>> startExam(
            @PathVariable Long examId,
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }

        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }

        // 检查是否已有进行中的考试记录
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStudentId, studentId)
                .eq(ExamRecord::getStatus, "ongoing");
        ExamRecord existingRecord = examRecordMapper.selectOne(recordWrapper);
        if (existingRecord != null) {
            // 返回已存在的记录
            return Result.success(buildExamDetail(existingRecord, exam, studentId));
        }

        // 创建新的考试记录
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setExamStudentId(studentId);
        record.setStatus("ongoing");
        record.setStartTime(LocalDateTime.now());
        record.setAnsweredCount(0);
        record.setTotalQuestions(0);
        examRecordMapper.insert(record);

        return Result.success(buildExamDetail(record, exam, studentId));
    }

    private Map<String, Object> buildExamDetail(ExamRecord record, Exam exam, Long studentId) {
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("examId", exam.getId());
        result.put("examName", exam.getName());
        result.put("duration", exam.getDuration());
        result.put("startTime", exam.getStartTime());
        result.put("endTime", exam.getEndTime());
        result.put("totalScore", exam.getTotalScore());
        result.put("recordStartTime", record.getStartTime());

        // 计算剩余时间
        long usedSeconds = java.time.Duration.between(record.getStartTime(), LocalDateTime.now()).getSeconds();
        int remainSeconds = exam.getDuration() * 60 - (int) usedSeconds;
        result.put("remainSeconds", Math.max(0, remainSeconds));

        // 获取试卷题目
        List<Map<String, Object>> questions = getExamQuestions(exam.getExamPaperId());
        result.put("questions", questions);
        result.put("totalQuestions", questions.size());

        return result;
    }

    private List<Map<String, Object>> getExamQuestions(Long examPaperId) {
        if (examPaperId == null) {
            return new ArrayList<>();
        }

        // 1. 通过 exam_paper_id 查询试卷关联的题目ID列表
        LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
        pqWrapper.eq(PaperQuestion::getPaperId, examPaperId)
                 .orderByAsc(PaperQuestion::getSortOrder);
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(pqWrapper);

        if (paperQuestions.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取题目ID，批量查询题目
        List<Long> questionIds = paperQuestions.stream()
                .map(PaperQuestion::getQuestionId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.in(Question::getId, questionIds)
                       .eq(Question::getStatus, 1);
        List<Question> questionList = questionMapper.selectList(questionWrapper);

        // 3. 构建 questionId -> score 映射（从试卷关联表取分值）
        Map<Long, BigDecimal> scoreMap = paperQuestions.stream()
                .collect(Collectors.toMap(PaperQuestion::getQuestionId, PaperQuestion::getScore, (a, b) -> a));

        return questionList.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());           // 前端期望 id，不是 questionId
            // 标准化类型名称（兼容 single/single_choice 等不同写法）
            String rawType = q.getType();
            String normalizedType;
            if (rawType != null && (rawType.contains("single") || rawType.equals("1"))) {
                normalizedType = "single_choice";
            } else if (rawType != null && (rawType.contains("multi") || rawType.equals("2"))) {
                normalizedType = "multi_choice";
            } else if (rawType != null && (rawType.contains("true_false") || rawType.contains("judge") || rawType.equals("3"))) {
                normalizedType = "true_false";
            } else if (rawType != null && (rawType.contains("fill") || rawType.equals("4"))) {
                normalizedType = "fill_blank";
            } else if (rawType != null && (rawType.contains("essay") || rawType.equals("5"))) {
                normalizedType = "essay";
            } else {
                normalizedType = rawType;
            }
            map.put("type", normalizedType);
            map.put("content", q.getContent());
            map.put("score", scoreMap.getOrDefault(q.getId(), q.getScore()));
            map.put("analysis", q.getAnalysis());

            // 单选/多选题从数据库查询选项
            if ("single_choice".equals(normalizedType) || "multi_choice".equals(normalizedType)) {
                LambdaQueryWrapper<QuestionOption> optionWrapper = new LambdaQueryWrapper<>();
                optionWrapper.eq(QuestionOption::getQuestionId, q.getId())
                            .orderByAsc(QuestionOption::getOptionLabel);
                List<QuestionOption> options = questionOptionMapper.selectList(optionWrapper);

                if (!options.isEmpty()) {
                    map.put("options", options.stream().map(opt ->
                        Map.of("label", opt.getOptionLabel(), "content", opt.getOptionContent(), "isCorrect", opt.getIsCorrect())
                    ).collect(Collectors.toList()));
                } else {
                    map.put("options", List.of(
                        Map.of("label", "A", "content", "", "isCorrect", false),
                        Map.of("label", "B", "content", "", "isCorrect", false),
                        Map.of("label", "C", "content", "", "isCorrect", false),
                        Map.of("label", "D", "content", "", "isCorrect", false)
                    ));
                }
            }

            return map;
        }).collect(Collectors.toList());
    }

    @Operation(summary = "保存答题进度")
    @PostMapping("/records/{recordId}/save")
    public Result<Void> saveProgress(
            @PathVariable Long recordId,
            @RequestBody Map<String, Object> body) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("考试记录不存在");
        }

        Exam exam = examMapper.selectById(record.getExamId());
        List<Map<String, Object>> answers = parseAnswers(body.get("answers"));
        persistAnswerRecords(record, exam, answers);

        // 更新答题数量
        record.setAnsweredCount(countAnswered(answers));
        if (exam != null && exam.getExamPaperId() != null) {
            record.setTotalQuestions(getPaperQuestionCount(exam.getExamPaperId()));
        }
        examRecordMapper.updateById(record);

        return Result.success("保存成功", null);
    }

    @Operation(summary = "恢复答题进度")
    @GetMapping("/records/{recordId}/restore")
    public Result<Map<String, Object>> restoreProgress(@PathVariable Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("考试记录不存在");
        }

        Exam exam = examMapper.selectById(record.getExamId());
        if (exam == null) {
            return Result.error("考试不存在");
        }

        Map<String, Object> result = buildExamDetail(record, exam, record.getStudentId());
        return Result.success(result);
    }

    @Operation(summary = "提交试卷")
    @PostMapping("/records/{recordId}/submit")
    public Result<Map<String, Object>> submitExam(
            @PathVariable Long recordId,
            @RequestBody Map<String, Object> body) {

        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("考试记录不存在");
        }

        Exam exam = examMapper.selectById(record.getExamId());
        if (exam == null) {
            return Result.error("考试不存在");
        }

        List<Map<String, Object>> answers = parseAnswers(body.get("answers"));
        persistAnswerRecords(record, exam, answers);
        ScoreResult scoreResult = settleRecord(record, exam);

        // 更新记录
        record.setStatus("submitted");
        record.setSubmitTime(LocalDateTime.now());
        record.setObjectiveScore(scoreResult.objectiveScore);
        record.setSubjectiveScore(scoreResult.subjectiveScore);
        record.setTotalScore(scoreResult.totalScore());
        record.setAnsweredCount(countAnswered(answers));
        if (exam.getExamPaperId() != null) {
            record.setTotalQuestions(getPaperQuestionCount(exam.getExamPaperId()));
        }

        // 计算用时
        if (record.getStartTime() != null) {
            long duration = java.time.Duration.between(record.getStartTime(), record.getSubmitTime()).getSeconds();
            record.setDurationSeconds(duration);
        }

        examRecordMapper.updateById(record);
        Grade grade = upsertGrade(record, exam, scoreResult);

        // 返回成绩
        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", scoreResult.totalScore());
        result.put("objectiveScore", scoreResult.objectiveScore);
        result.put("subjectiveScore", scoreResult.subjectiveScore);
        result.put("passScore", exam.getPassScore());
        result.put("isPassed", scoreResult.totalScore() >= exam.getPassScore().doubleValue());
        result.put("answeredCount", record.getAnsweredCount());
        result.put("durationSeconds", record.getDurationSeconds());
        result.put("gradingTaskCount", scoreResult.pendingTaskCount);
        if (grade != null) {
            result.put("gradeId", grade.getId());
            result.put("gradeStatus", grade.getStatus());
        }

        return Result.success(result);
    }

    private List<Map<String, Object>> parseAnswers(Object rawAnswers) {
        List<Map<String, Object>> answers = new ArrayList<>();
        if (rawAnswers instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> map) {
                    Map<String, Object> answer = new HashMap<>();
                    map.forEach((key, value) -> answer.put(String.valueOf(key), value));
                    answers.add(answer);
                }
            }
        } else if (rawAnswers instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Map<String, Object> answer = new HashMap<>();
                answer.put("questionId", entry.getKey());
                answer.put("answer", entry.getValue());
                answers.add(answer);
            }
        }
        return answers;
    }

    private void persistAnswerRecords(ExamRecord record, Exam exam, List<Map<String, Object>> answers) {
        if (answers == null || answers.isEmpty()) {
            return;
        }
        Map<Long, BigDecimal> scoreMap = getPaperScoreMap(exam != null ? exam.getExamPaperId() : null);
        for (Map<String, Object> item : answers) {
            Long questionId = toLong(item.get("questionId"));
            if (questionId == null) {
                continue;
            }
            Question question = questionMapper.selectById(questionId);
            if (question == null) {
                continue;
            }
            String studentAnswer = normalizeAnswer(item.get("answer"));
            BigDecimal fullScore = scoreMap.getOrDefault(questionId, question.getScore() != null ? question.getScore() : BigDecimal.ZERO);
            AnswerScore answerScore = scoreAnswer(question, studentAnswer, fullScore);

            AnswerRecord answerRecord = answerRecordMapper.selectOne(
                    new LambdaQueryWrapper<AnswerRecord>()
                            .eq(AnswerRecord::getExamRecordId, record.getId())
                            .eq(AnswerRecord::getQuestionId, questionId)
                            .last("LIMIT 1")
            );
            LocalDateTime now = LocalDateTime.now();
            if (answerRecord == null) {
                answerRecord = new AnswerRecord();
                answerRecord.setExamRecordId(record.getId());
                answerRecord.setQuestionId(questionId);
                answerRecord.setAnswerTimes(1);
                answerRecord.setFirstAnswerTime(now);
                answerRecord.setCreateTime(now);
            } else {
                answerRecord.setAnswerTimes((answerRecord.getAnswerTimes() == null ? 0 : answerRecord.getAnswerTimes()) + 1);
                if (answerRecord.getFirstAnswerTime() == null) {
                    answerRecord.setFirstAnswerTime(now);
                }
            }
            answerRecord.setStudentAnswer(studentAnswer);
            answerRecord.setIsCorrect(answerScore.isCorrect);
            answerRecord.setScore(answerScore.score);
            answerRecord.setLastModifyTime(now);
            if (answerRecord.getId() == null) {
                answerRecordMapper.insert(answerRecord);
            } else {
                answerRecordMapper.updateById(answerRecord);
            }
        }
    }

    private ScoreResult settleRecord(ExamRecord record, Exam exam) {
        List<AnswerRecord> answerRecords = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, record.getId())
        );
        double objectiveScore = 0.0;
        double subjectiveScore = 0.0;
        int pendingTaskCount = 0;
        for (AnswerRecord answerRecord : answerRecords) {
            Question question = answerRecord.getQuestionId() != null ? questionMapper.selectById(answerRecord.getQuestionId()) : null;
            if (question == null) {
                continue;
            }
            if (isManualQuestion(question.getType())) {
                if (answerRecord.getScore() != null) {
                    subjectiveScore += answerRecord.getScore();
                }
                pendingTaskCount += ensureGradingTask(record, answerRecord);
            } else if (answerRecord.getScore() != null) {
                objectiveScore += answerRecord.getScore();
            }
        }
        return new ScoreResult(objectiveScore, subjectiveScore, pendingTaskCount);
    }

    private int ensureGradingTask(ExamRecord record, AnswerRecord answerRecord) {
        LambdaQueryWrapper<GradingTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GradingTask::getExamRecordId, record.getId())
                .eq(GradingTask::getQuestionId, answerRecord.getQuestionId());
        if (gradingTaskMapper.selectCount(wrapper) > 0) {
            return 0;
        }
        GradingTask task = new GradingTask();
        task.setExamRecordId(record.getId());
        task.setQuestionId(answerRecord.getQuestionId());
        task.setStudentId(record.getStudentId());
        task.setStatus("pending");
        task.setCreateTime(LocalDateTime.now());
        gradingTaskMapper.insert(task);
        return 1;
    }

    private Grade upsertGrade(ExamRecord record, Exam exam, ScoreResult scoreResult) {
        Grade grade = gradeMapper.selectOne(
                new LambdaQueryWrapper<Grade>().eq(Grade::getExamRecordId, record.getId()).last("LIMIT 1")
        );
        if (grade == null) {
            grade = new Grade();
            grade.setExamRecordId(record.getId());
            grade.setExamId(record.getExamId());
            grade.setStudentId(record.getStudentId());
        }
        grade.setObjectiveScore(BigDecimal.valueOf(scoreResult.objectiveScore));
        grade.setSubjectiveScore(BigDecimal.valueOf(scoreResult.subjectiveScore));
        grade.setTotalScore(BigDecimal.valueOf(scoreResult.totalScore()));
        grade.setStatus(hasPendingGradingTask(record.getId()) ? "pending" : "published");
        grade.setPublishTime(LocalDateTime.now());
        if (grade.getId() == null) {
            gradeMapper.insert(grade);
        } else {
            gradeMapper.updateById(grade);
        }
        return grade;
    }

    private Map<Long, BigDecimal> getPaperScoreMap(Long examPaperId) {
        if (examPaperId == null) {
            return new HashMap<>();
        }
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, examPaperId)
        );
        return paperQuestions.stream()
                .filter(item -> item.getQuestionId() != null)
                .collect(Collectors.toMap(PaperQuestion::getQuestionId, PaperQuestion::getScore, (a, b) -> a));
    }

    private int getPaperQuestionCount(Long examPaperId) {
        return Math.toIntExact(paperQuestionMapper.selectCount(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, examPaperId)
        ));
    }

    private boolean hasPendingGradingTask(Long examRecordId) {
        return gradingTaskMapper.selectCount(
                new LambdaQueryWrapper<GradingTask>()
                        .eq(GradingTask::getExamRecordId, examRecordId)
                        .ne(GradingTask::getStatus, "completed")
        ) > 0;
    }

    private int countAnswered(List<Map<String, Object>> answers) {
        if (answers == null) {
            return 0;
        }
        return (int) answers.stream()
                .map(item -> normalizeAnswer(item.get("answer")))
                .filter(answer -> answer != null && !answer.isBlank())
                .count();
    }

    private AnswerScore scoreAnswer(Question question, String studentAnswer, BigDecimal fullScore) {
        if (isManualQuestion(question.getType())) {
            return new AnswerScore(null, null);
        }
        String correctAnswer = normalizeAnswer(question.getAnswer());
        if (studentAnswer == null || studentAnswer.isBlank() || correctAnswer == null || correctAnswer.isBlank()) {
            return new AnswerScore(false, 0.0);
        }
        String type = normalizeType(question.getType());
        if ("multi_choice".equals(type)) {
            Set<String> correctSet = splitAnswerSet(correctAnswer);
            Set<String> studentSet = splitAnswerSet(studentAnswer);
            if (correctSet.equals(studentSet)) {
                return new AnswerScore(true, fullScore.doubleValue());
            }
            if (!Collections.disjoint(correctSet, studentSet)) {
                return new AnswerScore(false, fullScore.doubleValue() * 0.5);
            }
            return new AnswerScore(false, 0.0);
        }
        boolean correct = "fill_blank".equals(type)
                ? studentAnswer.trim().equalsIgnoreCase(correctAnswer.trim()) || studentAnswer.trim().toLowerCase().contains(correctAnswer.trim().toLowerCase())
                : studentAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
        return new AnswerScore(correct, correct ? fullScore.doubleValue() : 0.0);
    }

    private boolean isManualQuestion(String type) {
        String normalizedType = normalizeType(type);
        return "essay".equals(normalizedType) || "code".equals(normalizedType);
    }

    private String normalizeType(String type) {
        if (type == null) return "";
        if (type.contains("single") || type.equals("1")) return "single_choice";
        if (type.contains("multi") || type.equals("2")) return "multi_choice";
        if (type.contains("true_false") || type.contains("judge") || type.equals("3")) return "true_false";
        if (type.contains("fill") || type.equals("4")) return "fill_blank";
        if (type.contains("essay") || type.equals("5")) return "essay";
        return type;
    }

    private Set<String> splitAnswerSet(String answer) {
        return Arrays.stream(answer.split("[,，]"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeAnswer(Object answer) {
        if (answer == null) {
            return "";
        }
        if (answer instanceof List<?> list) {
            return list.stream().map(String::valueOf).sorted().collect(Collectors.joining(","));
        }
        return String.valueOf(answer).trim();
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private record AnswerScore(Boolean isCorrect, Double score) {}
    private record ScoreResult(double objectiveScore, double subjectiveScore, int pendingTaskCount) {
        double totalScore() {
            return objectiveScore + subjectiveScore;
        }
    }

    @Operation(summary = "获取已完成的考试列表")
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> historyExams(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }

        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getStudentId, studentId)
                .ne(ExamRecord::getStatus, "ongoing")
                .orderByDesc(ExamRecord::getSubmitTime);
        List<ExamRecord> records = examRecordMapper.selectList(wrapper);

        List<Map<String, Object>> result = records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("recordId", record.getId());
            map.put("examId", record.getExamId());
            map.put("totalScore", record.getTotalScore());
            map.put("objectiveScore", record.getObjectiveScore());
            map.put("subjectiveScore", record.getSubjectiveScore());
            map.put("status", record.getStatus());
            map.put("submitTime", record.getSubmitTime());

            Exam exam = examMapper.selectById(record.getExamId());
            if (exam != null) {
                map.put("examName", exam.getName());
                map.put("passScore", exam.getPassScore());
                map.put("isPassed", record.getTotalScore() >= exam.getPassScore().doubleValue());
            }

            Grade grade = gradeMapper.selectOne(new LambdaQueryWrapper<Grade>()
                    .eq(Grade::getExamRecordId, record.getId())
                    .eq(Grade::getStudentId, studentId)
                    .last("LIMIT 1"));
            if (grade != null) {
                map.put("gradeId", grade.getId());
                map.put("gradeStatus", grade.getStatus());
                map.put("publishTime", grade.getPublishTime());
            }

            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }
}
