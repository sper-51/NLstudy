package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.*;
import com.nl.nlstudy.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "管理端-仪表盘")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final SysUserMapper sysUserMapper;
    private final ExamMapper examMapper;
    private final CourseMapper courseMapper;
    private final QuestionMapper questionMapper;
    private final GradingTaskMapper gradingTaskMapper;
    private final GradeMapper gradeMapper;
    private final LoginLogMapper loginLogMapper;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> result = new HashMap<>();

        // 当前在线人数：最近30分钟内有登录记录的用户数
        LocalDateTime onlineThreshold = LocalDateTime.now().minusMinutes(30);
        Long onlineCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLog>()
                        .ge(LoginLog::getCreateTime, onlineThreshold)
                        .eq(LoginLog::getStatus, 1)
        );
        result.put("onlineCount", onlineCount != null ? onlineCount : 0);

        // 全站总注册用户数（status=1, deleted=0由@TableLogic自动处理）
        Long totalUsers = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1)
        );
        result.put("totalUsers", totalUsers != null ? totalUsers : 0);

        // 按角色统计用户数
        Long adminCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1).eq(SysUser::getRole, "admin")
        );
        Long teacherCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1).eq(SysUser::getRole, "teacher")
        );
        Long studentCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1).eq(SysUser::getRole, "student")
        );
        result.put("adminCount", adminCount != null ? adminCount : 0);
        result.put("teacherCount", teacherCount != null ? teacherCount : 0);
        result.put("studentCount", studentCount != null ? studentCount : 0);

        // 今日考试场次（开始时间是今天）
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);
        Long todayExams = examMapper.selectCount(
                new LambdaQueryWrapper<Exam>()
                        .ge(Exam::getStartTime, todayStart)
                        .le(Exam::getStartTime, todayEnd)
        );
        result.put("todayExams", todayExams != null ? todayExams : 0);

        // 今日已结束考试
        Long todayCompletedExams = examMapper.selectCount(
                new LambdaQueryWrapper<Exam>()
                        .eq(Exam::getStatus, "finished")
                        .ge(Exam::getEndTime, todayStart)
                        .le(Exam::getEndTime, todayEnd)
        );
        result.put("todayCompletedExams", todayCompletedExams != null ? todayCompletedExams : 0);

        // 系统并发（最近1小时登录人数作为并发参考）
        LocalDateTime concurrentThreshold = LocalDateTime.now().minusHours(1);
        Long concurrent = loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLog>()
                        .ge(LoginLog::getCreateTime, concurrentThreshold)
                        .eq(LoginLog::getStatus, 1)
        );
        result.put("concurrent", concurrent != null ? concurrent : 0);
        result.put("peak", 0); // 无历史峰值数据，返回0

        // 系统资源使用（运行时实时采集）
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double memoryPercent = maxMemory > 0 ? usedMemory * 100.0 / maxMemory : 0.0;
        result.put("cpuUsage", roundOneDecimal(readCpuUsage()));
        result.put("memoryUsage", roundOneDecimal(memoryPercent));

        // 实时进行中的考试：status='ongoing' 或 (status='published' 且在时间范围内)，限制5条
        LocalDateTime now = LocalDateTime.now();
        List<Exam> liveExamList = examMapper.selectList(
                new LambdaQueryWrapper<Exam>()
                        .and(w -> w.eq(Exam::getStatus, "ongoing")
                                .or(inner -> inner.eq(Exam::getStatus, "published")
                                        .le(Exam::getStartTime, now)
                                        .ge(Exam::getEndTime, now)))
                        .last("LIMIT 5")
        );

        List<Map<String, Object>> liveExams = new ArrayList<>();
        if (liveExamList != null) {
            // 批量获取关联的课程和教师信息
            Set<Long> courseIds = liveExamList.stream()
                    .map(Exam::getCourseId).filter(Objects::nonNull).collect(Collectors.toSet());
            Set<Long> teacherIds = liveExamList.stream()
                    .map(Exam::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());

            Map<Long, String> courseNameMap = new HashMap<>();
            if (!courseIds.isEmpty()) {
                List<Course> courses = courseMapper.selectList(
                        new LambdaQueryWrapper<Course>().in(Course::getId, courseIds)
                );
                if (courses != null) {
                    courseNameMap = courses.stream()
                            .collect(Collectors.toMap(Course::getId, Course::getName, (a, b) -> a));
                }
            }

            Map<Long, String> teacherNameMap = new HashMap<>();
            if (!teacherIds.isEmpty()) {
                List<SysUser> teachers = sysUserMapper.selectList(
                        new LambdaQueryWrapper<SysUser>().in(SysUser::getId, teacherIds)
                );
                if (teachers != null) {
                    teacherNameMap = teachers.stream()
                            .collect(Collectors.toMap(SysUser::getId,
                                    u -> u.getRealName() != null ? u.getRealName() : u.getUsername(),
                                    (a, b) -> a));
                }
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Exam exam : liveExamList) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", exam.getId());
                item.put("examName", exam.getName());
                item.put("department", courseNameMap.getOrDefault(exam.getCourseId(), ""));
                item.put("teacherName", teacherNameMap.getOrDefault(exam.getTeacherId(), ""));
                item.put("startTime", exam.getStartTime() != null ? exam.getStartTime().format(dtf) : "");
                item.put("endTime", exam.getEndTime() != null ? exam.getEndTime().format(dtf) : "");
                item.put("participants", exam.getStudentCount() != null ? exam.getStudentCount() : 0);
                liveExams.add(item);
            }
        }
        result.put("liveExams", liveExams);

        // 待批改任务数
        Long pendingGradingTasks = gradingTaskMapper.selectCount(
                new LambdaQueryWrapper<GradingTask>().eq(GradingTask::getStatus, "pending")
        );
        result.put("pendingGradingTasks", pendingGradingTasks != null ? pendingGradingTasks : 0);
        result.put("teachingStats", buildTeachingStats());
        result.put("qualityStats", buildQualityStats());

        return Result.success(result);
    }

    private Map<String, Object> buildTeachingStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Grade> publishedGrades = gradeMapper.selectList(
                new LambdaQueryWrapper<Grade>()
                        .isNotNull(Grade::getTotalScore)
                        .eq(Grade::getStatus, "published")
        );

        if (publishedGrades == null || publishedGrades.isEmpty()) {
            stats.put("scoreTrend", Map.of("labels", List.of(), "passRates", List.of(), "avgScores", List.of()));
            stats.put("classCompare", Map.of("classNames", List.of(), "avgScores", List.of(), "passRates", List.of()));
            stats.put("scoreDist", List.of());
            return stats;
        }

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal maxScore = null;
        BigDecimal minScore = null;
        int passed = 0;
        for (Grade grade : publishedGrades) {
            BigDecimal score = grade.getTotalScore();
            sum = sum.add(score);
            if (maxScore == null || score.compareTo(maxScore) > 0) maxScore = score;
            if (minScore == null || score.compareTo(minScore) < 0) minScore = score;
            BigDecimal passScore = resolvePassScore(grade.getExamId());
            if (score.compareTo(passScore) >= 0) passed++;
        }
        stats.put("passRate", percent(passed, publishedGrades.size()));
        stats.put("avgScore", sum.divide(BigDecimal.valueOf(publishedGrades.size()), 1, RoundingMode.HALF_UP));
        stats.put("maxScore", maxScore);
        stats.put("minScore", minScore);

        List<Exam> exams = examMapper.selectList(new LambdaQueryWrapper<Exam>().isNotNull(Exam::getStudentCount));
        int expected = exams == null ? 0 : exams.stream().map(Exam::getStudentCount).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        int submitted = exams == null ? 0 : exams.stream().map(Exam::getSubmitCount).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        if (expected > 0) stats.put("participationRate", percent(submitted, expected));

        stats.put("scoreDist", buildScoreDistribution(publishedGrades));
        stats.put("scoreTrend", buildScoreTrend(publishedGrades));
        stats.put("classCompare", buildCourseCompare(publishedGrades));
        return stats;
    }

    private Map<String, Object> buildQualityStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Grade> publishedGrades = gradeMapper.selectList(
                new LambdaQueryWrapper<Grade>()
                        .isNotNull(Grade::getTotalScore)
                        .eq(Grade::getStatus, "published")
        );
        stats.put("collegeRank", buildCourseCompare(publishedGrades));
        stats.put("questionType", buildQuestionTypeDistribution());
        stats.put("difficultyRadar", buildDifficultyDistribution());
        return stats;
    }

    private BigDecimal resolvePassScore(Long examId) {
        if (examId == null) return BigDecimal.valueOf(60);
        Exam exam = examMapper.selectById(examId);
        if (exam == null || exam.getPassScore() == null) return BigDecimal.valueOf(60);
        return exam.getPassScore();
    }

    private BigDecimal percent(int numerator, int denominator) {
        if (denominator <= 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 1, RoundingMode.HALF_UP);
    }

    private List<Integer> buildScoreDistribution(List<Grade> grades) {
        int[] buckets = new int[5];
        for (Grade grade : grades) {
            double score = grade.getTotalScore().doubleValue();
            if (score >= 90) buckets[0]++;
            else if (score >= 80) buckets[1]++;
            else if (score >= 70) buckets[2]++;
            else if (score >= 60) buckets[3]++;
            else buckets[4]++;
        }
        return Arrays.stream(buckets).boxed().collect(Collectors.toList());
    }

    private Map<String, Object> buildScoreTrend(List<Grade> grades) {
        List<Exam> exams = examMapper.selectList(
                new LambdaQueryWrapper<Exam>()
                        .isNotNull(Exam::getCreateTime)
                        .orderByAsc(Exam::getCreateTime)
        );
        if (exams == null || exams.isEmpty()) {
            return Map.of("labels", List.of(), "passRates", List.of(), "avgScores", List.of());
        }

        Map<Long, List<Grade>> gradesByExam = grades.stream()
                .filter(g -> g.getExamId() != null)
                .collect(Collectors.groupingBy(Grade::getExamId));
        List<String> labels = new ArrayList<>();
        List<BigDecimal> passRates = new ArrayList<>();
        List<BigDecimal> avgScores = new ArrayList<>();
        for (Exam exam : exams) {
            List<Grade> examGrades = gradesByExam.get(exam.getId());
            if (examGrades == null || examGrades.isEmpty()) continue;
            BigDecimal sum = examGrades.stream()
                    .map(Grade::getTotalScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal passScore = exam.getPassScore() != null ? exam.getPassScore() : BigDecimal.valueOf(60);
            long passed = examGrades.stream().filter(g -> g.getTotalScore().compareTo(passScore) >= 0).count();
            labels.add(exam.getName());
            passRates.add(percent((int) passed, examGrades.size()));
            avgScores.add(sum.divide(BigDecimal.valueOf(examGrades.size()), 1, RoundingMode.HALF_UP));
            if (labels.size() >= 8) break;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("passRates", passRates);
        result.put("avgScores", avgScores);
        return result;
    }

    private Map<String, Object> buildCourseCompare(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return Map.of("names", List.of(), "classNames", List.of(), "passRates", List.of(), "avgScores", List.of(), "totalScores", List.of());
        }
        Map<Long, Exam> examMap = examMapper.selectList(new LambdaQueryWrapper<Exam>())
                .stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(Exam::getId, e -> e, (a, b) -> a));
        Map<Long, Course> courseMap = courseMapper.selectList(new LambdaQueryWrapper<Course>())
                .stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Course::getId, c -> c, (a, b) -> a));

        Map<Long, List<Grade>> gradesByCourse = grades.stream()
                .filter(g -> g.getExamId() != null && examMap.containsKey(g.getExamId()))
                .collect(Collectors.groupingBy(g -> examMap.get(g.getExamId()).getCourseId()));

        List<String> names = new ArrayList<>();
        List<BigDecimal> passRates = new ArrayList<>();
        List<BigDecimal> avgScores = new ArrayList<>();
        List<BigDecimal> totalScores = new ArrayList<>();
        gradesByCourse.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .limit(8)
                .forEach(entry -> {
                    Long courseId = entry.getKey();
                    List<Grade> courseGrades = entry.getValue();
                    BigDecimal sum = courseGrades.stream().map(Grade::getTotalScore).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal avg = sum.divide(BigDecimal.valueOf(courseGrades.size()), 1, RoundingMode.HALF_UP);
                    long passed = courseGrades.stream().filter(g -> g.getTotalScore().compareTo(resolvePassScore(g.getExamId())) >= 0).count();
                    Course course = courseMap.get(courseId);
                    names.add(course != null ? course.getName() : "课程" + courseId);
                    passRates.add(percent((int) passed, courseGrades.size()));
                    avgScores.add(avg);
                    totalScores.add(avg);
                });

        Map<String, Object> result = new HashMap<>();
        result.put("names", names);
        result.put("classNames", names);
        result.put("passRates", passRates);
        result.put("avgScores", avgScores);
        result.put("totalScores", totalScores);
        return result;
    }

    private List<Map<String, Object>> buildQuestionTypeDistribution() {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getStatus, 1));
        if (questions == null || questions.isEmpty()) return List.of();
        Map<String, Long> grouped = questions.stream()
                .filter(q -> q.getType() != null)
                .collect(Collectors.groupingBy(Question::getType, Collectors.counting()));
        return grouped.entrySet().stream().map(entry -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", questionTypeName(entry.getKey()));
            item.put("value", entry.getValue());
            return item;
        }).collect(Collectors.toList());
    }

    private List<Long> buildDifficultyDistribution() {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getStatus, 1));
        if (questions == null || questions.isEmpty()) return List.of();
        List<String> order = List.of("easy", "medium", "hard");
        Map<String, Long> grouped = questions.stream()
                .filter(q -> q.getDifficulty() != null)
                .collect(Collectors.groupingBy(Question::getDifficulty, Collectors.counting()));
        return order.stream().map(d -> grouped.getOrDefault(d, 0L)).collect(Collectors.toList());
    }

    private String questionTypeName(String type) {
        return switch (type) {
            case "single_choice" -> "单选题";
            case "multi_choice" -> "多选题";
            case "true_false" -> "判断题";
            case "fill_blank" -> "填空题";
            case "essay" -> "简答题";
            default -> type;
        };
    }

    @Operation(summary = "获取仪表盘趋势数据")
    @GetMapping("/trend")
    public Result<Map<String, Object>> getDashboardTrend(
            @RequestParam(defaultValue = "7") String range) {

        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> examCount = new ArrayList<>();
        List<Integer> userActivity = new ArrayList<>();
        List<Integer> avgScore = new ArrayList<>();

        int days = switch (range) {
            case "30" -> 30;
            case "90" -> 90;
            default -> 7;
        };

        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            labels.add(date.format(labelFormatter));

            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            // 每天创建的考试数量
            Long dailyExamCount = examMapper.selectCount(
                    new LambdaQueryWrapper<Exam>()
                            .ge(Exam::getCreateTime, dayStart)
                            .le(Exam::getCreateTime, dayEnd)
            );
            examCount.add(dailyExamCount != null ? dailyExamCount.intValue() : 0);

            // 每天注册的用户数量
            Long dailyUserCount = sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .ge(SysUser::getCreateTime, dayStart)
                            .le(SysUser::getCreateTime, dayEnd)
                            .eq(SysUser::getStatus, 1)
            );
            userActivity.add(dailyUserCount != null ? dailyUserCount.intValue() : 0);

            // 每天平均分（从grade表的publishTime聚合）
            List<Grade> dayGrades = gradeMapper.selectList(
                    new LambdaQueryWrapper<Grade>()
                            .isNotNull(Grade::getTotalScore)
                            .ge(Grade::getPublishTime, dayStart)
                            .le(Grade::getPublishTime, dayEnd)
            );
            if (dayGrades != null && !dayGrades.isEmpty()) {
                BigDecimal sum = dayGrades.stream()
                        .map(Grade::getTotalScore)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int avg = sum.divide(BigDecimal.valueOf(dayGrades.size()), 0, RoundingMode.HALF_UP).intValue();
                avgScore.add(avg);
            } else {
                avgScore.add(0);
            }
        }

        result.put("labels", labels);
        result.put("examCount", examCount);
        result.put("userActivity", userActivity);
        result.put("avgScore", avgScore);

        return Result.success(result);
    }

    private double readCpuUsage() {
        try {
            com.sun.management.OperatingSystemMXBean bean =
                    (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double load = bean.getCpuLoad();
            if (load < 0) {
                load = bean.getProcessCpuLoad();
            }
            return load >= 0 ? load * 100.0 : 0.0;
        } catch (Exception ignored) {
            return 0.0;
        }
    }

    private double roundOneDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
