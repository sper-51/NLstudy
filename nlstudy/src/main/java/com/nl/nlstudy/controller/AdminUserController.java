package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理员-用户管理")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final ISysUserService userService;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "获取用户列表")
    @GetMapping
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<SysUser> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(item -> item
                    .like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword));
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = userService.page(pageParam, wrapper);
        List<Map<String, Object>> records = new ArrayList<>();
        for (SysUser user : result.getRecords()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", user.getId());
            row.put("username", user.getUsername());
            row.put("realName", user.getRealName());
            row.put("role", user.getRole());
            row.put("email", user.getEmail());
            row.put("phone", user.getPhone());
            row.put("avatar", user.getAvatar());
            row.put("status", user.getStatus());
            row.put("lastLoginTime", user.getLastLoginTime());
            row.put("lastLoginIp", user.getLastLoginIp());
            row.put("createTime", user.getCreateTime());

            if ("teacher".equals(user.getRole())) {
                row.put("title", queryString("SELECT title FROM teacher_info WHERE user_id = ? LIMIT 1", user.getId()));
            }
            if ("student".equals(user.getRole())) {
                row.put("studentNo", user.getUsername());
                Map<String, Object> classInfo = queryClassInfo(user.getId());
                if (classInfo != null) {
                    row.put("classId", classInfo.get("classId"));
                    row.put("className", classInfo.get("className"));
                }
            }
            records.add(row);
        }
        return Result.success(PageResult.of(records, page, pageSize, result.getTotal()));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Void> create(@RequestBody SysUser user) {
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.save(user);
        return Result.success();
    }

    @Operation(summary = "编辑用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        userService.updateById(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/resetPassword")
    public Result<String> resetPassword(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 重置密码为默认值 123456 并加密
        String newPassword = passwordEncoder.encode("123456");
        user.setPassword(newPassword);
        userService.updateById(user);

        return Result.success("密码已重置为：123456");
    }

    @Operation(summary = "启用/禁用用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setStatus(body.get("status"));
            userService.updateById(user);
        }
        return Result.success();
    }

    @Operation(summary = "批量导入学生")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> importStudents(@RequestParam("file") MultipartFile file) {
        // TODO: 解析Excel文件并批量导入学生
        return Result.success(Map.of(
                "successCount", 0,
                "failCount", 0,
                "message", "批量导入功能开发中，请稍后再试"
        ));
    }

    @Operation(summary = "导出用户")
    @GetMapping("/export")
    public Result<String> exportUsers(@RequestParam(required = false) String role) {
        // TODO: 导出用户数据为Excel
        return Result.success("导出功能开发中");
    }

    private String queryString(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, String.class, args);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> queryClassInfo(Long studentId) {
        try {
            return jdbcTemplate.queryForMap("""
                    SELECT sc.class_id AS classId, c.name AS className
                    FROM student_class sc
                    JOIN sys_class c ON c.id = sc.class_id
                    WHERE sc.student_id = ?
                    ORDER BY sc.create_time DESC, sc.id DESC
                    LIMIT 1
                    """, studentId);
        } catch (Exception e) {
            return null;
        }
    }
}
