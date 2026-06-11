package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.entity.ExamPaper;
import com.nl.nlstudy.entity.LoginLog;
import com.nl.nlstudy.entity.OnlineUser;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.ExamMapper;
import com.nl.nlstudy.mapper.ExamPaperMapper;
import com.nl.nlstudy.mapper.LoginLogMapper;
import com.nl.nlstudy.mapper.OnlineUserMapper;
import com.nl.nlstudy.mapper.QuestionMapper;
import com.nl.nlstudy.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "管理端-系统监控")
@RestController
@RequestMapping("/admin/monitor")
@RequiredArgsConstructor
public class AdminMonitorController {

    private final ExamMapper examMapper;
    private final CourseMapper courseMapper;
    private final QuestionMapper questionMapper;
    private final ExamPaperMapper examPaperMapper;
    private final SysUserMapper sysUserMapper;
    private final LoginLogMapper loginLogMapper;
    private final OnlineUserMapper onlineUserMapper;

    @Operation(summary = "获取在线趋势数据")
    @GetMapping("/online/trend")
    public Result<Map<String, Object>> getOnlineTrend(
            @RequestParam(defaultValue = "24") int hours) {

        Map<String, Object> result = new HashMap<>();
        List<String> hourLabels = new ArrayList<>();
        List<Integer> teacherData = new ArrayList<>();
        List<Integer> studentData = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00");

        for (int i = hours - 1; i >= 0; i--) {
            LocalDateTime time = LocalDateTime.now().minusHours(i);
            LocalDateTime hourStart = time.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime hourEnd = hourStart.plusHours(1);
            hourLabels.add(time.format(formatter));
            teacherData.add(countLoginUsersByRole("teacher", hourStart, hourEnd));
            studentData.add(countLoginUsersByRole("student", hourStart, hourEnd));
        }

        result.put("hours", hourLabels);
        result.put("teacherData", teacherData);
        result.put("studentData", studentData);

        return Result.success(result);
    }

    @Operation(summary = "获取实时在线统计")
    @GetMapping("/online/current")
    public Result<Map<String, Object>> getCurrentOnline() {
        Map<String, Object> result = new HashMap<>();

        LocalDateTime onlineThreshold = getOnlineThreshold();
        List<Map<String, Object>> onlineUsers = buildOnlineUsers(onlineThreshold);
        long onlineTeachers = onlineUsers.stream().filter(item -> "teacher".equals(item.get("role"))).count();
        long onlineStudents = onlineUsers.stream().filter(item -> "student".equals(item.get("role"))).count();
        result.put("onlineTeachers", onlineTeachers);
        result.put("onlineStudents", onlineStudents);
        result.put("onlineUsers", onlineUsers);
        result.put("list", onlineUsers);
        result.put("todayLoginCount", countTodayLogins());

        // 查询真实的考试和课程统计数据
        long totalExams = examMapper.selectCount(null);
        long ongoingExams = examMapper.selectCount(
            new LambdaQueryWrapper<Exam>().eq(Exam::getStatus, "ongoing"));
        long totalQuestions = questionMapper.selectCount(
            new LambdaQueryWrapper<Question>().eq(Question::getStatus, 1));
        long totalPapers = examPaperMapper.selectCount(
            new LambdaQueryWrapper<ExamPaper>().eq(ExamPaper::getStatus, 1));
        long totalCourses = courseMapper.selectCount(
            new LambdaQueryWrapper<Course>().eq(Course::getStatus, 1));
        long totalUsers = sysUserMapper.selectCount(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "student"));

        result.put("totalExams", totalExams);
        result.put("ongoingExams", ongoingExams);
        result.put("totalQuestions", totalQuestions);
        result.put("totalPapers", totalPapers);
        result.put("totalCourses", totalCourses);
        result.put("totalStudents", totalUsers);

        return Result.success(result);
    }

    @Operation(summary = "强制用户下线")
    @PostMapping("/onlineUsers/{userId}/logout")
    public Result<Void> forceLogout(@PathVariable Long userId) {
        OnlineUser onlineUser = onlineUserMapper.selectOne(
                new LambdaQueryWrapper<OnlineUser>().eq(OnlineUser::getUserId, userId).last("LIMIT 1")
        );
        if (onlineUser == null) {
            return Result.error("在线会话不存在");
        }
        onlineUser.setStatus(0);
        onlineUser.setLastActiveTime(LocalDateTime.now());
        onlineUserMapper.updateById(onlineUser);
        return Result.success("已标记为离线", null);
    }

    @Operation(summary = "获取考试状态统计")
    @GetMapping("/exam/status")
    public Result<Map<String, Object>> getExamStatus() {
        Map<String, Object> result = new HashMap<>();

        // 查询真实的考试数据
        List<Exam> allExams = examMapper.selectList(
            new LambdaQueryWrapper<Exam>()
                .orderByDesc(Exam::getStartTime)
                .last("LIMIT 10"));

        List<Map<String, Object>> examList = new ArrayList<>();
        for (Exam exam : allExams) {
            // 获取课程名称
            String courseName = "未知课程";
            if (exam.getCourseId() != null) {
                Course course = courseMapper.selectById(exam.getCourseId());
                if (course != null) {
                    courseName = course.getName();
                }
            }

            Map<String, Object> examMap = new HashMap<>();
            examMap.put("id", exam.getId());
            examMap.put("name", exam.getName());
            examMap.put("courseName", courseName);
            examMap.put("startTime", exam.getStartTime() != null ? exam.getStartTime().toString() : "");
            examMap.put("endTime", exam.getEndTime() != null ? exam.getEndTime().toString() : "");
            examMap.put("status", exam.getStatus());
            examMap.put("totalStudents", exam.getStudentCount() != null ? exam.getStudentCount() : 0);
            examMap.put("completedStudents", exam.getSubmitCount() != null ? exam.getSubmitCount() : 0);
            examList.add(examMap);
        }

        result.put("exams", examList);
        result.put("totalExams", examMapper.selectCount(null));
        result.put("ongoingCount", examMapper.selectCount(
            new LambdaQueryWrapper<Exam>().eq(Exam::getStatus, "ongoing")));
        result.put("pendingCount", examMapper.selectCount(
            new LambdaQueryWrapper<Exam>().eq(Exam::getStatus, "pending")));
        result.put("endedCount", examMapper.selectCount(
            new LambdaQueryWrapper<Exam>().in(Exam::getStatus, Arrays.asList("finished", "ended"))));

        return Result.success(result);
    }

    @Operation(summary = "获取系统健康状态")
    @GetMapping("/health")
    public Result<Map<String, Object>> getHealthStatus() {
        Map<String, Object> result = new HashMap<>();

        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();

        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double memoryPercent = maxMemory > 0 ? usedMemory * 100.0 / maxMemory : 0.0;
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();

        File root = new File(System.getProperty("user.dir")).getAbsoluteFile();
        long totalDiskBytes = root.getTotalSpace();
        long freeDiskBytes = root.getFreeSpace();
        long usedDiskBytes = Math.max(0, totalDiskBytes - freeDiskBytes);

        double cpuUsage = readCpuUsage();
        double diskUsage = totalDiskBytes > 0 ? usedDiskBytes * 100.0 / totalDiskBytes : 0.0;
        int processors = Runtime.getRuntime().availableProcessors();

        Map<String, Object> jvm = new LinkedHashMap<>();
        jvm.put("heapUsed", bytesToMb(heap.getUsed()));
        jvm.put("heapMax", bytesToMb(heap.getMax()));
        jvm.put("nonHeapUsed", bytesToMb(nonHeap.getUsed()));
        jvm.put("nonHeapMax", bytesToMb(nonHeap.getMax()));
        jvm.put("directUsed", 0);
        jvm.put("directMax", 0);
        jvm.put("activeThreads", threadBean.getThreadCount());
        jvm.put("daemonThreads", threadBean.getDaemonThreadCount());
        jvm.put("peakThreads", threadBean.getPeakThreadCount());
        jvm.put("totalStartedThreads", threadBean.getTotalStartedThreadCount());

        long gcCount = 0;
        long gcTime = 0;
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (gcBean.getCollectionCount() > 0) {
                gcCount += gcBean.getCollectionCount();
            }
            if (gcBean.getCollectionTime() > 0) {
                gcTime += gcBean.getCollectionTime();
            }
        }
        jvm.put("gcCount", gcCount);
        jvm.put("gcTotalTime", gcTime);
        jvm.put("youngGcCount", gcCount);
        jvm.put("youngGcTime", gcTime);
        jvm.put("fullGcCount", 0);
        jvm.put("fullGcTime", 0);

        result.put("status", "healthy");
        result.put("database", "connected");
        result.put("cpuUsage", round(cpuUsage));
        result.put("memUsage", round(memoryPercent));
        result.put("memoryUsage", String.format(Locale.ROOT, "%.1f%%", memoryPercent));
        result.put("diskUsage", round(diskUsage));
        result.put("cpuCores", processors);
        result.put("usedMem", round(bytesToGb(usedMemory)));
        result.put("totalMem", round(bytesToGb(maxMemory)));
        result.put("usedDisk", round(bytesToGb(usedDiskBytes)));
        result.put("totalDisk", round(bytesToGb(totalDiskBytes)));
        result.put("diskPath", root.getPath());
        result.put("systemLoad", round(readSystemLoad()));
        result.put("load1m", round(readSystemLoad()));
        result.put("load5m", 0);
        result.put("load15m", 0);
        result.put("uptime", formatDuration(uptimeMillis));
        result.put("jvm", jvm);

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

    private double readSystemLoad() {
        double load = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        return load > 0 ? load : 0.0;
    }

    private long bytesToMb(long bytes) {
        if (bytes <= 0) return 0;
        return Math.round(bytes / 1024.0 / 1024.0);
    }

    private double bytesToGb(long bytes) {
        if (bytes <= 0) return 0.0;
        return bytes / 1024.0 / 1024.0 / 1024.0;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        if (days > 0) {
            return days + "天" + hours + "小时";
        }
        if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        }
        return minutes + "分钟";
    }

    private int countLoginUsersByRole(String role, LocalDateTime start, LocalDateTime end) {
        Set<Long> userIds = loginLogMapper.selectList(
                new LambdaQueryWrapper<LoginLog>()
                        .eq(LoginLog::getStatus, 1)
                        .ge(LoginLog::getCreateTime, start)
                        .lt(LoginLog::getCreateTime, end)
        ).stream().map(LoginLog::getUserId).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        if (userIds.isEmpty()) return 0;
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getId, userIds)
                        .eq(SysUser::getRole, role)
                        .eq(SysUser::getStatus, 1)
        );
        return count == null ? 0 : count.intValue();
    }

    private List<Map<String, Object>> buildOnlineUsers(LocalDateTime onlineThreshold) {
        List<OnlineUser> sessions = onlineUserMapper.selectList(
                new LambdaQueryWrapper<OnlineUser>()
                        .eq(OnlineUser::getStatus, 1)
                        .ge(OnlineUser::getLastActiveTime, onlineThreshold)
                        .orderByDesc(OnlineUser::getLastActiveTime)
        );
        if (sessions.isEmpty()) return List.of();
        List<Long> userIds = sessions.stream().map(OnlineUser::getUserId).filter(Objects::nonNull).toList();
        if (userIds.isEmpty()) return List.of();
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Long, SysUser> userMap = users.stream().collect(java.util.stream.Collectors.toMap(SysUser::getId, user -> user));

        List<Map<String, Object>> result = new ArrayList<>();
        for (OnlineUser session : sessions) {
            SysUser user = userMap.get(session.getUserId());
            if (user == null || !"teacher".equals(user.getRole()) && !"student".equals(user.getRole())) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", user.getId());
            item.put("username", user.getUsername());
            item.put("realName", user.getRealName());
            item.put("role", user.getRole());
            item.put("loginTime", session.getLoginTime());
            item.put("lastActiveTime", session.getLastActiveTime());
            item.put("ipAddress", session.getIpAddress());
            result.add(item);
        }
        return result;
    }

    private LocalDateTime getOnlineThreshold() {
        return LocalDateTime.now().minusMinutes(5);
    }

    private long countTodayLogins() {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        Long count = loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLog>()
                        .eq(LoginLog::getStatus, 1)
                        .ge(LoginLog::getCreateTime, start)
        );
        return count == null ? 0 : count;
    }
}
