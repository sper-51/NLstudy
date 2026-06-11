package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.CourseCode;
import com.nl.nlstudy.entity.CourseSelection;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.mapper.CourseCodeMapper;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.CourseSelectionMapper;
import com.nl.nlstudy.mapper.ExamMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "教师端-课程管理")
@RestController
@RequestMapping("/teacher/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseMapper courseMapper;
    private final CourseCodeMapper courseCodeMapper;
    private final CourseSelectionMapper courseSelectionMapper;
    private final ExamMapper examMapper;
    private final com.nl.nlstudy.mapper.SysUserMapper sysUserMapper;

    @Operation(summary = "获取课程列表")
    @GetMapping
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long teacherId) {

        Page<Course> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        // 按当前登录教师过滤
        if (teacherId != null) {
            wrapper.eq(Course::getTeacherId, teacherId);
        }
        wrapper.orderByDesc(Course::getCreateTime);

        Page<Course> result = courseMapper.selectPage(pageParam, wrapper);
        
        // 转换数据格式，添加课程码和学生数量
        java.util.List<Map<String, Object>> courseList = new java.util.ArrayList<>();
        for (Course course : result.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", course.getId());
            item.put("name", course.getName());
            item.put("code", course.getCode());
            item.put("teacherId", course.getTeacherId());
            item.put("semesterId", course.getSemesterId());
            item.put("credits", course.getCredits());
            item.put("hours", course.getHours());
            item.put("description", course.getDescription());
            item.put("status", course.getStatus());
            item.put("createTime", course.getCreateTime());
            
            // 查询课程码
            CourseCode courseCode = courseCodeMapper.selectOne(
                new LambdaQueryWrapper<CourseCode>()
                    .eq(CourseCode::getCourseId, course.getId())
                    .eq(CourseCode::getStatus, 1)
            );
            if (courseCode != null) {
                Map<String, Object> codeMap = new LinkedHashMap<>();
                codeMap.put("code", courseCode.getCode());
                codeMap.put("expireTime", courseCode.getExpireTime());
                codeMap.put("maxUses", courseCode.getMaxUses());
                codeMap.put("usedCount", courseCode.getUsedCount());
                item.put("courseCode", courseCode.getCode());
                item.put("courseCodeInfo", codeMap);
            }
            
            // 查询学生数量
            Long studentCount = courseSelectionMapper.selectCount(
                new LambdaQueryWrapper<CourseSelection>()
                    .eq(CourseSelection::getCourseId, course.getId())
                    .eq(CourseSelection::getStatus, 1)
            );
            item.put("studentCount", studentCount);
            
            // 查询考试数量
            Long examCount = examMapper.selectCount(
                new LambdaQueryWrapper<Exam>()
                    .eq(Exam::getCourseId, course.getId())
                    .eq(Exam::getStatus, "published")
            );
            item.put("examCount", examCount);
            
            courseList.add(item);
        }
        
        return Result.success(PageResult.of(courseList, page, pageSize, result.getTotal()));
    }

    @Operation(summary = "创建课程")
    @PostMapping
    public Result<Course> create(@RequestBody Course course, @RequestHeader("X-User-Id") Long teacherId) {
        course.setTeacherId(teacherId);
        courseMapper.insert(course);
        return Result.success(course);
    }

    @Operation(summary = "更新课程")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        courseMapper.updateById(course);
        return Result.success();
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/{id}")
    public Result<Object> getDetail(@PathVariable Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", course.getId());
        result.put("name", course.getName());
        result.put("code", course.getCode());
        result.put("teacherId", course.getTeacherId());
        result.put("semesterId", course.getSemesterId());
        result.put("credits", course.getCredits());
        result.put("hours", course.getHours());
        result.put("description", course.getDescription());
        result.put("status", course.getStatus());
        
        // 查询课程码
        CourseCode courseCode = courseCodeMapper.selectOne(
            new LambdaQueryWrapper<CourseCode>()
                .eq(CourseCode::getCourseId, course.getId())
                .eq(CourseCode::getStatus, 1)
        );
        if (courseCode != null) {
            Map<String, Object> codeMap = new LinkedHashMap<>();
            codeMap.put("code", courseCode.getCode());
            codeMap.put("expireTime", courseCode.getExpireTime());
            codeMap.put("maxUses", courseCode.getMaxUses());
            codeMap.put("usedCount", courseCode.getUsedCount());
            result.put("courseCode", codeMap);
        }
        
        // 查询学生数量
        Long studentCount = courseSelectionMapper.selectCount(
            new LambdaQueryWrapper<CourseSelection>()
                .eq(CourseSelection::getCourseId, course.getId())
                .eq(CourseSelection::getStatus, 1)
        );
        result.put("studentCount", studentCount);
        
        // 查询考试数量
        Long examCount = examMapper.selectCount(
            new LambdaQueryWrapper<Exam>()
                .eq(Exam::getCourseId, course.getId())
                .eq(Exam::getStatus, "published")
        );
        result.put("examCount", examCount);
        
        return Result.success(result);
    }

    @Operation(summary = "生成/刷新课程码")
    @PostMapping("/{id}/generateCode")
    public Result<Map<String, Object>> generateCode(@PathVariable Long id) {
        String code = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 将生成的课程码保存到 course 表的 code 字段
        Course course = courseMapper.selectById(id);
        if (course != null) {
            course.setCode(code);
            courseMapper.updateById(course);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("shareLink", "/join?code=" + code);
        return Result.success(result);
    }

    @Operation(summary = "获取选课学生列表")
    @GetMapping("/{courseId}/students")
    public Result<PageResult<Map<String, Object>>> getStudents(
            @PathVariable Long courseId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, courseId);
        wrapper.eq(CourseSelection::getStatus, 1);
        
        // 查询总数量
        Long total = courseSelectionMapper.selectCount(wrapper);
        
        // 手动分页查询
        wrapper.last("LIMIT " + (page - 1) * pageSize + ", " + pageSize);
        java.util.List<CourseSelection> selections = courseSelectionMapper.selectList(wrapper);
        
        // 查询学生信息
        java.util.List<Map<String, Object>> studentList = new java.util.ArrayList<>();
        for (CourseSelection selection : selections) {
            SysUser student = sysUserMapper.selectById(selection.getStudentId());
            
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("studentId", selection.getStudentId());
            item.put("username", student != null ? student.getUsername() : "");
            item.put("realName", student != null ? student.getRealName() : "");
            item.put("selectTime", selection.getSelectTime());
            item.put("status", selection.getStatus());
            studentList.add(item);
        }
        
        return Result.success(PageResult.of(studentList, page, pageSize, total));
    }

    @Operation(summary = "移除选课学生")
    @DeleteMapping("/{courseId}/students/{studentId}")
    public Result<Void> removeStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        // 删除 course_selection 表中对应记录
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, courseId)
               .eq(CourseSelection::getStudentId, studentId);
        courseSelectionMapper.delete(wrapper);
        return Result.success();
    }
}
