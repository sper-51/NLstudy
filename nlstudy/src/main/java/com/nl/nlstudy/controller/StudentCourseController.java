package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.CourseSelection;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.entity.Grade;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.CourseSelectionMapper;
import com.nl.nlstudy.mapper.ExamMapper;
import com.nl.nlstudy.mapper.ExamRecordMapper;
import com.nl.nlstudy.mapper.GradeMapper;
import com.nl.nlstudy.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "学生端-课程")
@RestController
@RequestMapping("/student/courses")
@RequiredArgsConstructor
public class StudentCourseController {

    private final CourseMapper courseMapper;
    private final CourseSelectionMapper courseSelectionMapper;
    private final SysUserMapper sysUserMapper;
    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final GradeMapper gradeMapper;

    private Long getCurrentStudentId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    @Operation(summary = "获取我的课程列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long studentId = getCurrentStudentId(request);
        if (studentId == null) {
            return Result.error("未提供用户ID");
        }

        // 查询学生已选的课程
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, studentId);
        List<CourseSelection> selections = courseSelectionMapper.selectList(wrapper);

        if (selections.isEmpty()) {
            return Result.success(List.of());
        }

        List<Long> courseIds = selections.stream()
                .map(CourseSelection::getCourseId)
                .collect(Collectors.toList());

        List<Course> courses = courseMapper.selectBatchIds(courseIds);

        // 获取所有教师ID
        List<Long> teacherIds = courses.stream()
                .map(Course::getTeacherId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> teacherNameMap = teacherIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(teacherIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        List<Map<String, Object>> result = courses.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("code", c.getCode() != null ? c.getCode() : "");
            map.put("description", c.getDescription() != null ? c.getDescription() : "");
            map.put("teacherId", c.getTeacherId());
            map.put("teacherName", teacherNameMap.getOrDefault(c.getTeacherId(), "未知教师"));
            map.put("credits", c.getCredits() != null ? c.getCredits() : 0);
            map.put("hours", c.getHours() != null ? c.getHours() : 0);
            map.put("status", c.getStatus());
            map.put("progress", 0);
            map.put("pendingExamCount", 0);
            map.put("wrongQuestionCount", 0);
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "使用课程码加课")
    @PostMapping("/join")
    public Result<Object> join(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String courseCode = body.get("courseCode");
        if (courseCode == null || courseCode.isBlank()) {
            courseCode = body.get("code");
        }
        Long studentId = getCurrentStudentId(request);

        if (studentId == null) {
            return Result.error("未提供用户ID");
        }

        if (courseCode == null || courseCode.isBlank()) {
            return Result.error("课程码不能为空");
        }
        courseCode = courseCode.trim().toUpperCase();

        // 根据课程码查询课程
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getCode, courseCode);
        Course course = courseMapper.selectOne(courseWrapper);

        if (course == null) {
            return Result.error("课程码无效");
        }

        // 检查是否已经选过该课
        LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
        selectionWrapper.eq(CourseSelection::getStudentId, studentId)
                       .eq(CourseSelection::getCourseId, course.getId());
        CourseSelection existingSelection = courseSelectionMapper.selectOne(selectionWrapper);
        if (existingSelection != null) {
            return Result.error("您已选过该课程");
        }

        // 创建选课记录
        CourseSelection selection = new CourseSelection();
        selection.setStudentId(studentId);
        selection.setCourseId(course.getId());
        selection.setStatus(1); // 1表示已选课
        courseSelectionMapper.insert(selection);

        Map<String, Object> result = new HashMap<>();
        result.put("courseId", course.getId());
        result.put("courseName", course.getName());
        return Result.success(result);
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/{courseId}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", course.getId());
        result.put("name", course.getName());
        result.put("code", course.getCode() != null ? course.getCode() : "");
        result.put("description", course.getDescription());
        result.put("teacherId", course.getTeacherId());
        result.put("credits", course.getCredits() != null ? course.getCredits() : 0);
        result.put("hours", course.getHours() != null ? course.getHours() : 0);
        result.put("status", course.getStatus());

        // 教师名称
        if (course.getTeacherId() != null) {
            SysUser teacher = sysUserMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                result.put("teacherName", teacher.getRealName());
            }
        }

        // 查询该课程下可对学生展示的考试：已发布/进行中/已结束都应出现在课程详情
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
        examWrapper.eq(Exam::getCourseId, courseId)
                   .in(Exam::getStatus, List.of("published", "ongoing", "finished"));
        List<Exam> exams = examMapper.selectList(examWrapper);

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Map<String, Object>> examList = exams.stream().map(exam -> {
            Map<String, Object> em = new HashMap<>();
            em.put("id", exam.getId());
            em.put("examName", exam.getName());
            em.put("startTime", exam.getStartTime());
            em.put("endTime", exam.getEndTime());
            em.put("duration", exam.getDuration());
            em.put("totalScore", exam.getTotalScore());
            em.put("passScore", exam.getPassScore());
            em.put("examType", exam.getExamType());

            // 判断考试状态
            String status;
            if ("finished".equals(exam.getStatus())) {
                status = "finished";
            } else if (now.isBefore(exam.getStartTime())) {
                status = "pending";
            } else if (now.isAfter(exam.getEndTime())) {
                status = "finished";
            } else {
                status = "ongoing";
            }
            em.put("status", status);

            // 如果提供了studentId，检查该学生的考试记录
            if (studentId != null) {
                LambdaQueryWrapper<com.nl.nlstudy.entity.ExamRecord> recordWrapper =
                    new LambdaQueryWrapper<>();
                recordWrapper.eq(com.nl.nlstudy.entity.ExamRecord::getExamId, exam.getId())
                             .eq(com.nl.nlstudy.entity.ExamRecord::getStudentId, studentId)
                             .last("LIMIT 1");
                com.nl.nlstudy.entity.ExamRecord record = examRecordMapper.selectOne(recordWrapper);
                if (record != null) {
                    em.put("myScore", record.getTotalScore());
                    if ("submitted".equals(record.getStatus()) || "auto_submitted".equals(record.getStatus())) {
                        em.put("status", "completed");
                    }
                    Grade grade = gradeMapper.selectOne(new LambdaQueryWrapper<Grade>()
                            .eq(Grade::getExamRecordId, record.getId())
                            .eq(Grade::getStudentId, studentId)
                            .last("LIMIT 1"));
                    if (grade != null) {
                        em.put("gradeId", grade.getId());
                        em.put("gradeStatus", grade.getStatus());
                    }
                }
            }
            return em;
        }).collect(Collectors.toList());

        result.put("exams", examList);
        return Result.success(result);
    }
}
