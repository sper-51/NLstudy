package com.nl.nlstudy.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.LoginLog;
import com.nl.nlstudy.entity.OnlineUser;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.mapper.LoginLogMapper;
import com.nl.nlstudy.mapper.OnlineUserMapper;
import com.nl.nlstudy.mapper.SysUserMapper;
import com.nl.nlstudy.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper userMapper;
    private final LoginLogMapper loginLogMapper;
    private final OnlineUserMapper onlineUserMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "通用登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        return doLogin(body.get("username"), body.get("password"), null, request);
    }

    @Operation(summary = "学生登录")
    @PostMapping("/student/login")
    public Result<Map<String, Object>> loginStudent(@RequestBody Map<String, String> body, HttpServletRequest request) {
        return doLogin(body.get("username"), body.get("password"), "student", request);
    }

    @Operation(summary = "教师登录")
    @PostMapping("/teacher/login")
    public Result<Map<String, Object>> loginTeacher(@RequestBody Map<String, String> body, HttpServletRequest request) {
        return doLogin(body.get("username"), body.get("password"), "teacher", request);
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/admin/login")
    public Result<Map<String, Object>> loginAdmin(@RequestBody Map<String, String> body, HttpServletRequest request) {
        return doLogin(body.get("username"), body.get("password"), "admin", request);
    }

    private Result<Map<String, Object>> doLogin(String username, String password, String role, HttpServletRequest request) {
        if (username == null || password == null) {
            return Result.error(400, "用户名和密码不能为空");
        }

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (role != null && !role.isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }
        SysUser user = userMapper.selectOne(wrapper);
        if (user == null) {
            recordLoginLog(null, username, 0, "用户名或密码错误", request);
            return Result.error(401, "用户名或密码错误");
        }

        if (!isPasswordMatched(password, user.getPassword())) {
            recordLoginLog(user, username, 0, "用户名或密码错误", request);
            return Result.error(401, "用户名或密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            recordLoginLog(user, username, 0, "账号已被禁用", request);
            return Result.error(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LocalDateTime now = LocalDateTime.now();
        user.setLastLoginTime(now);
        user.setLastLoginIp(getClientIp(request));
        userMapper.updateById(user);
        recordLoginLog(user, username, 1, null, request);
        upsertOnlineUser(user, token, request, now);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "realName", user.getRealName() != null ? user.getRealName() : user.getUsername(),
                "role", user.getRole(),
                "avatar", user.getAvatar() != null ? user.getAvatar() : ""
        ));
        return Result.success("登录成功", result);
    }

    private void recordLoginLog(SysUser user, String username, Integer status, String errorMessage, HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setUserId(user != null ? user.getId() : null);
        log.setUsername(username != null ? username : "");
        log.setStatus(status);
        log.setIpAddress(getClientIp(request));
        log.setUserAgent(request != null ? request.getHeader("User-Agent") : "");
        log.setLoginMethod("password");
        log.setErrorMessage(errorMessage);
        loginLogMapper.insert(log);
    }

    private void upsertOnlineUser(SysUser user, String token, HttpServletRequest request, LocalDateTime now) {
        OnlineUser onlineUser = onlineUserMapper.selectOne(
                new LambdaQueryWrapper<OnlineUser>().eq(OnlineUser::getUserId, user.getId()).last("LIMIT 1")
        );
        if (onlineUser == null) {
            onlineUser = new OnlineUser();
            onlineUser.setUserId(user.getId());
            onlineUser.setCreateTime(now);
        }
        onlineUser.setUsername(user.getUsername());
        onlineUser.setRole(user.getRole());
        onlineUser.setToken(token);
        onlineUser.setLoginTime(now);
        onlineUser.setLastActiveTime(now);
        onlineUser.setIpAddress(getClientIp(request));
        onlineUser.setStatus(1);
        if (onlineUser.getId() == null) {
            onlineUserMapper.insert(onlineUser);
        } else {
            onlineUserMapper.updateById(onlineUser);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "";
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    private boolean isPasswordMatched(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        if (rawPassword.equals(storedPassword)) {
            return true;
        }
        return SecureUtil.md5(rawPassword).equalsIgnoreCase(storedPassword);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (userId == null && authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (jwtUtil.validateToken(token)) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        if (userId != null) {
            OnlineUser onlineUser = onlineUserMapper.selectOne(
                    new LambdaQueryWrapper<OnlineUser>().eq(OnlineUser::getUserId, userId).last("LIMIT 1")
            );
            if (onlineUser != null) {
                onlineUser.setStatus(0);
                onlineUser.setLastActiveTime(LocalDateTime.now());
                onlineUserMapper.updateById(onlineUser);
            }
        }
        return Result.success("退出成功", null);
    }

    @Operation(summary = "获取当前用户")
    @GetMapping("/currentUser")
    public Result<SysUser> getCurrentUser(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error(401, "未登录或登录已失效");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody Map<String, String> body) {
        if (userId == null) {
            return Result.error(401, "未登录或登录已失效");
        }
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null || newPassword.length() < 6) {
            return Result.error(400, "新密码长度不能少于6位");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        if (!isPasswordMatched(oldPassword, user.getPassword())) {
            return Result.error(400, "旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success("密码修改成功", null);
    }

    @Operation(summary = "忘记密码")
    @PostMapping("/forgotPassword")
    public Result<String> forgotPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        return Result.success("请联系管理员重置密码");
    }

    @Operation(summary = "重置密码")
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String newPassword = body.getOrDefault("newPassword", "123456");
        if (username == null || username.isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success("重置密码成功");
    }
}
