package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Course;
import com.nl.nlstudy.entity.CourseSelection;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.mapper.CourseMapper;
import com.nl.nlstudy.mapper.CourseSelectionMapper;
import com.nl.nlstudy.mapper.SysClassMapper;
import com.nl.nlstudy.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "学生端-个人信息")
@RestController
@RequestMapping("/student/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final SysUserMapper sysUserMapper;
    private final SysClassMapper sysClassMapper;
    private final CourseSelectionMapper courseSelectionMapper;
    private final CourseMapper courseMapper;

    @Operation(summary = "获取我的信息")
    @GetMapping
    public Result<Map<String, Object>> getProfile(@RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) {
            return Result.error(401, "未登录或登录已失效");
        }
        SysUser user = sysUserMapper.selectById(studentId);
        if (user == null || !"student".equals(user.getRole())) {
            return Result.error(404, "学生不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("avatar", user.getAvatar());
        result.put("status", user.getStatus());
        result.put("lastLoginTime", user.getLastLoginTime());
        result.put("classes", sysClassMapper.selectClassesByStudentId(studentId));

        List<CourseSelection> selections = courseSelectionMapper.selectList(new LambdaQueryWrapper<CourseSelection>()
                .eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getStatus, 1));
        List<Map<String, Object>> courses = selections.stream().map(selection -> {
            Course course = courseMapper.selectById(selection.getCourseId());
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", selection.getCourseId());
            item.put("selectTime", selection.getSelectTime());
            if (course != null) {
                item.put("courseName", course.getName());
                item.put("courseCode", course.getCode());
                item.put("credits", course.getCredits());
                item.put("hours", course.getHours());
            }
            return item;
        }).toList();
        result.put("courses", courses);

        return Result.success(result);
    }

    @Operation(summary = "更新我的可编辑信息")
    @PutMapping
    public Result<Map<String, Object>> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId,
            @RequestBody Map<String, String> body) {
        if (studentId == null) {
            return Result.error(401, "未登录或登录已失效");
        }
        SysUser user = sysUserMapper.selectById(studentId);
        if (user == null || !"student".equals(user.getRole())) {
            return Result.error(404, "学生不存在");
        }

        if (body.containsKey("realName")) user.setRealName(body.get("realName"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        if (body.containsKey("avatar")) user.setAvatar(body.get("avatar"));
        sysUserMapper.updateById(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("avatar", user.getAvatar());
        result.put("role", user.getRole());
        return Result.success("个人信息已更新", result);
    }
}
