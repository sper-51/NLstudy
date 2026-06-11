package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.LoginLog;
import com.nl.nlstudy.entity.SysLog;
import com.nl.nlstudy.mapper.LoginLogMapper;
import com.nl.nlstudy.mapper.SysLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端-日志审计")
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final LoginLogMapper loginLogMapper;
    private final SysLogMapper sysLogMapper;

    @Operation(summary = "分页查询登录日志")
    @GetMapping("/login")
    public Result<PageResult<LoginLog>> getLoginLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        Page<LoginLog> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            wrapper.like(LoginLog::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(LoginLog::getStatus, status);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(LoginLog::getCreateTime, LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(LoginLog::getCreateTime, LocalDateTime.parse(endTime));
        }

        wrapper.orderByDesc(LoginLog::getCreateTime);

        Page<LoginLog> result = loginLogMapper.selectPage(pageParam, wrapper);
        return Result.success(PageResult.of(result.getRecords(), page, pageSize, result.getTotal()));
    }

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/operation")
    public Result<PageResult<Map<String, Object>>> getOperationLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        try {
            Page<SysLog> pageParam = new Page<>(page, pageSize);
            LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

            if (username != null && !username.isEmpty()) {
                wrapper.like(SysLog::getUsername, username);
            }
            if (module != null && !module.isEmpty()) {
                wrapper.like(SysLog::getModule, module);
            }
            if (startTime != null && !startTime.isEmpty()) {
                wrapper.ge(SysLog::getCreateTime, LocalDateTime.parse(startTime));
            }
            if (endTime != null && !endTime.isEmpty()) {
                wrapper.le(SysLog::getCreateTime, LocalDateTime.parse(endTime));
            }

            wrapper.orderByDesc(SysLog::getCreateTime);
            Page<SysLog> result = sysLogMapper.selectPage(pageParam, wrapper);

            List<Map<String, Object>> logList = new ArrayList<>();
            for (SysLog log : result.getRecords()) {
                Map<String, Object> logMap = new HashMap<>();
                logMap.put("id", log.getId());
                logMap.put("userId", log.getUserId());
                logMap.put("username", log.getUsername());
                logMap.put("operation", log.getOperation());
                logMap.put("module", log.getModule());
                logMap.put("method", log.getMethod());
                logMap.put("params", log.getParams());
                logMap.put("result", log.getResult());
                logMap.put("ipAddress", log.getIpAddress());
                logMap.put("executionTime", log.getExecutionTime());
                logMap.put("createTime", log.getCreateTime() != null ? log.getCreateTime().toString() : "");
                logList.add(logMap);
            }

            return Result.success(PageResult.of(logList, page, pageSize, result.getTotal()));
        } catch (Exception ex) {
            return Result.success(PageResult.of(new ArrayList<>(), page, pageSize, 0));
        }
    }

    @Operation(summary = "查询日志统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayLoginCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLog>().ge(LoginLog::getCreateTime, todayStart));
        result.put("todayLoginCount", todayLoginCount);

        long successLoginCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLog>().eq(LoginLog::getStatus, 1));
        result.put("successLoginCount", successLoginCount);

        long failedLoginCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLog>().eq(LoginLog::getStatus, 0));
        result.put("failedLoginCount", failedLoginCount);

        long operationCount = 0;
        try {
            operationCount = sysLogMapper.selectCount(null);
        } catch (Exception ignored) {
        }
        result.put("operationCount", operationCount);

        return Result.success(result);
    }
}

