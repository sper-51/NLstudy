package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.ExamRecord;
import com.nl.nlstudy.entity.Grade;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.mapper.ExamRecordMapper;
import com.nl.nlstudy.mapper.GradeMapper;
import com.nl.nlstudy.mapper.SysUserMapper;
import com.nl.nlstudy.mapper.ExamMapper;
import com.nl.nlstudy.mapper.SysClassMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "教师端-成绩管理")
@RestController
@RequestMapping("/teacher/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeMapper gradeMapper;
    private final ExamRecordMapper examRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final ExamMapper examMapper;
    private final SysClassMapper sysClassMapper;
    private final JdbcTemplate jdbcTemplate;

    @Operation(summary = "获取成绩列表")
    @GetMapping
    public Result<PageResult<Grade>> list(
            @RequestParam(required = false) Long examId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        Page<Grade> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        if (examId != null) {
            wrapper.eq(Grade::getExamId, examId);
        }
        if (teacherId != null) {
            // 通过Exam表过滤属于该教师的考试
            LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
            examWrapper.eq(Exam::getTeacherId, teacherId).select(Exam::getId);
            List<Exam> exams = examMapper.selectList(examWrapper);
            if (exams.isEmpty()) {
                return Result.success(PageResult.of(new ArrayList<>(), page, pageSize, 0));
            }
            List<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toList());
            wrapper.in(Grade::getExamId, examIds);
        }
        wrapper.orderByDesc(Grade::getTotalScore);
        Page<Grade> result = gradeMapper.selectPage(pageParam, wrapper);
        return Result.success(PageResult.of(result.getRecords(), page, pageSize, result.getTotal()));
    }

    @Operation(summary = "获取成绩详情")
    @GetMapping("/{gradeId}")
    public Result<Grade> detail(@PathVariable Long gradeId) {
        Grade grade = gradeMapper.selectById(gradeId);
        return Result.success(grade);
    }

    @Operation(summary = "获取成绩统计")
    @GetMapping("/exam/{examId}/statistics")
    public Result<Map<String, Object>> statistics(@PathVariable Long examId, @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }
        // 验证考试是否属于当前教师
        if (teacherId != null) {
            if (!teacherId.equals(exam.getTeacherId())) {
                return Result.error("无权访问该考试的成绩统计");
            }
        }
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getExamId, examId);
        List<Grade> grades = gradeMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();

        if (grades == null || grades.isEmpty()) {
            result.put("totalStudents", 0);
            result.put("submittedCount", 0);
            result.put("avgScore", 0.0);
            result.put("maxScore", 0.0);
            result.put("minScore", 0.0);
            result.put("passCount", 0);
            result.put("passRate", 0.0);
            result.put("excellentCount", 0);
            result.put("excellentRate", 0.0);
            result.put("topStudent", "-");
            result.put("bottomStudent", "-");
            result.put("scoreDistribution", Map.of());
            return Result.success(result);
        }

        int total = grades.size();
        double avgScore = grades.stream()
                .map(Grade::getTotalScore)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average().orElse(0.0);

        double maxScore = grades.stream()
                .map(Grade::getTotalScore)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .max().orElse(0.0);

        double minScore = grades.stream()
                .map(Grade::getTotalScore)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .min().orElse(0.0);

        double passLine = exam.getPassScore() != null ? exam.getPassScore().doubleValue() : 60.0;
        long passCount = grades.stream()
                .map(Grade::getTotalScore)
                .filter(Objects::nonNull)
                .filter(s -> s.doubleValue() >= passLine)
                .count();
        long excellentCount = grades.stream()
                .map(Grade::getTotalScore)
                .filter(Objects::nonNull)
                .filter(s -> s.doubleValue() >= 90)
                .count();

        double passRate = total > 0 ? (passCount * 100.0 / total) : 0.0;
        double excellentRate = total > 0 ? (excellentCount * 100.0 / total) : 0.0;

        // 成绩分布
        Map<String, Long> distribution = grades.stream()
                .map(Grade::getTotalScore)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        s -> {
                            double score = s.doubleValue();
                            if (score >= 90) return "90-100";
                            if (score >= 80) return "80-89";
                            if (score >= 70) return "70-79";
                            if (score >= 60) return "60-69";
                            return "0-59";
                        },
                        Collectors.counting()
                ));

        result.put("totalStudents", total);
        result.put("submittedCount", total);
        result.put("avgScore", BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP).doubleValue());
        result.put("maxScore", BigDecimal.valueOf(maxScore).setScale(1, RoundingMode.HALF_UP).doubleValue());
        result.put("minScore", BigDecimal.valueOf(minScore).setScale(1, RoundingMode.HALF_UP).doubleValue());
        result.put("passCount", passCount);
        result.put("passRate", BigDecimal.valueOf(passRate).setScale(1, RoundingMode.HALF_UP).doubleValue());
        result.put("excellentCount", excellentCount);
        result.put("excellentRate", BigDecimal.valueOf(excellentRate).setScale(1, RoundingMode.HALF_UP).doubleValue());
        result.put("topStudent", resolveStudentName(findStudentByScore(grades, maxScore)));
        result.put("bottomStudent", resolveStudentName(findStudentByScore(grades, minScore)));
        result.put("scoreDistribution", distribution);

        return Result.success(result);
    }

    @Operation(summary = "发布成绩")
    @PutMapping("/{gradeId}/publish")
    public Result<Void> publish(@PathVariable Long gradeId) {
        Grade grade = new Grade();
        grade.setId(gradeId);
        grade.setStatus("published");
        grade.setPublishTime(java.time.LocalDateTime.now());
        gradeMapper.updateById(grade);
        return Result.success();
    }

    @Operation(summary = "成绩分布")
    @GetMapping("/distribution")
    public Result<Map<String, Object>> distribution(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        if (examId != null) {
            wrapper.eq(Grade::getExamId, examId);
        }
        if (courseId != null) {
            wrapper.inSql(Grade::getExamId, "SELECT id FROM exam WHERE course_id = " + courseId);
        }
        if (teacherId != null) {
            // 通过Exam表过滤属于该教师的考试
            LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
            examWrapper.eq(Exam::getTeacherId, teacherId).select(Exam::getId);
            List<Exam> exams = examMapper.selectList(examWrapper);
            if (!exams.isEmpty()) {
                List<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toList());
                wrapper.in(Grade::getExamId, examIds);
            } else {
                // 没有属于该教师的考试，返回空结果
                Map<String, Long> emptyDistribution = new LinkedHashMap<>();
                emptyDistribution.put("90-100", 0L);
                emptyDistribution.put("80-89", 0L);
                emptyDistribution.put("70-79", 0L);
                emptyDistribution.put("60-69", 0L);
                emptyDistribution.put("0-59", 0L);
                return Result.success(new HashMap<>(emptyDistribution));
            }
        }
        List<Grade> grades = gradeMapper.selectList(wrapper);

        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("90-100", 0L);
        distribution.put("80-89", 0L);
        distribution.put("70-79", 0L);
        distribution.put("60-69", 0L);
        distribution.put("0-59", 0L);

        if (grades != null && !grades.isEmpty()) {
            for (Grade g : grades) {
                if (g.getTotalScore() == null) continue;
                double score = g.getTotalScore().doubleValue();
                if (score >= 90) distribution.put("90-100", distribution.get("90-100") + 1);
                else if (score >= 80) distribution.put("80-89", distribution.get("80-89") + 1);
                else if (score >= 70) distribution.put("70-79", distribution.get("70-79") + 1);
                else if (score >= 60) distribution.put("60-69", distribution.get("60-69") + 1);
                else distribution.put("0-59", distribution.get("0-59") + 1);
            }
        }

        return Result.success(new HashMap<>(distribution));
    }

    @Operation(summary = "班级对比")
    @GetMapping("/classComparison")
    public Result<List<Map<String, Object>>> classComparison(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        // 尝试从数据库实现班级对比
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        if (examId != null) {
            wrapper.eq(Grade::getExamId, examId);
        }
        if (courseId != null) {
            wrapper.inSql(Grade::getExamId, "SELECT id FROM exam WHERE course_id = " + courseId);
        }
        if (teacherId != null) {
            // 通过Exam表过滤属于该教师的考试
            LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
            examWrapper.eq(Exam::getTeacherId, teacherId).select(Exam::getId);
            List<Exam> exams = examMapper.selectList(examWrapper);
            if (exams.isEmpty()) {
                return Result.success(List.of());
            }
            List<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toList());
            wrapper.in(Grade::getExamId, examIds);
        }
        List<Grade> grades = gradeMapper.selectList(wrapper);

        if (grades == null || grades.isEmpty()) {
            return Result.success(List.of());
        }

        Set<Long> studentIds = grades.stream()
                .map(Grade::getStudentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> studentClassMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            String placeholders = studentIds.stream().map(id -> "?").collect(Collectors.joining(","));
            String sql = "SELECT sc.student_id, c.name FROM student_class sc " +
                    "JOIN sys_class c ON c.id = sc.class_id AND c.deleted = 0 " +
                    "WHERE sc.student_id IN (" + placeholders + ")";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, studentIds.toArray());
            for (Map<String, Object> row : rows) {
                Object sid = row.get("student_id");
                Object cname = row.get("name");
                if (sid != null && cname != null) {
                    studentClassMap.put(((Number) sid).longValue(), String.valueOf(cname));
                }
            }
        }

        Map<String, List<Grade>> groupedByClass = grades.stream()
                .collect(Collectors.groupingBy(g -> studentClassMap.getOrDefault(g.getStudentId(), "未分班"), LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Grade>> entry : groupedByClass.entrySet()) {
            List<Grade> classGrades = entry.getValue();
            double avgScore = classGrades.stream()
                    .map(Grade::getTotalScore)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average().orElse(0.0);
            double maxScore = classGrades.stream()
                    .map(Grade::getTotalScore)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .max().orElse(0.0);
            long passCount = classGrades.stream()
                    .map(Grade::getTotalScore)
                    .filter(Objects::nonNull)
                    .filter(s -> s.doubleValue() >= 60)
                    .count();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("className", entry.getKey());
            item.put("studentCount", classGrades.size());
            item.put("avgScore", Math.round(avgScore * 100) / 100.0);
            item.put("maxScore", Math.round(maxScore * 100) / 100.0);
            item.put("passCount", passCount);
            item.put("passRate", classGrades.size() > 0 ? Math.round(passCount * 100.0 / classGrades.size() * 10) / 10.0 : 0.0);
            result.add(item);
        }

        result.sort(Comparator.comparing(item -> String.valueOf(item.get("className"))));
        return Result.success(result);

    }

    @Operation(summary = "学生排名")
    @GetMapping("/exam/{examId}/ranking")
    public Result<PageResult<Map<String, Object>>> ranking(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {
        // 验证考试是否属于当前教师
        if (teacherId != null) {
            Exam exam = examMapper.selectById(examId);
            if (exam == null || !teacherId.equals(exam.getTeacherId())) {
                return Result.error("无权访问该考试的学生排名");
            }
        }

        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getExamId, examId);
        wrapper.orderByDesc(Grade::getTotalScore);
        Page<Grade> pageParam = new Page<>(page, pageSize);
        Page<Grade> result = gradeMapper.selectPage(pageParam, wrapper);

        // 获取学生信息
        Set<Long> studentIds = result.getRecords().stream()
                .map(Grade::getStudentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, SysUser> userMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(studentIds);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        }

        // 转换结果
        int rank = (int) ((page - 1) * pageSize + 1);
        List<Map<String, Object>> records = new ArrayList<>();
        for (Grade grade : result.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", grade.getId());
            item.put("studentId", grade.getStudentId());
            item.put("studentName", grade.getStudentId() != null && userMap.containsKey(grade.getStudentId())
                    ? userMap.get(grade.getStudentId()).getRealName() : "学生" + grade.getStudentId());
            item.put("totalScore", grade.getTotalScore());
            item.put("objectiveScore", grade.getObjectiveScore());
            item.put("subjectiveScore", grade.getSubjectiveScore());
            item.put("rank", rank++);
            item.put("percentile", grade.getPercentile());
            item.put("status", grade.getStatus());
            records.add(item);
        }

        return Result.success(PageResult.of(records, page, pageSize, result.getTotal()));
    }

    @Operation(summary = "导出成绩")
    @GetMapping("/export")
    public Result<String> exportGrades(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long courseId) {
        // TODO: 实现Excel导出
        return Result.success("成绩导出功能开发中，请稍后再试");
    }

    private Long findStudentByScore(List<Grade> grades, double targetScore) {
        return grades.stream()
                .filter(g -> g.getTotalScore() != null)
                .filter(g -> BigDecimal.valueOf(targetScore).compareTo(g.getTotalScore()) == 0)
                .map(Grade::getStudentId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private String resolveStudentName(Long studentId) {
        if (studentId == null) {
            return "-";
        }
        SysUser student = sysUserMapper.selectById(studentId);
        return student != null && student.getRealName() != null ? student.getRealName() : "学生" + studentId;
    }
}
