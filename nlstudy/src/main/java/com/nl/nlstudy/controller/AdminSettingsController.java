package com.nl.nlstudy.controller;

import com.nl.nlstudy.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理端-系统配置")
@RestController
@RequestMapping("/admin/settings")
public class AdminSettingsController {

    @Operation(summary = "获取系统配置")
    @GetMapping
    public Result<Map<String, Object>> getSettings() {
        Map<String, Object> data = new HashMap<>();
        data.put("siteName", "智能在线考试平台");
        data.put("allowRegister", false);
        data.put("defaultPassword", "123456");
        data.put("examAntiCheatEnabled", true);
        data.put("reviewEnabled", true);
        data.put("maintenanceMode", false);
        data.put("mailEnabled", false);
        data.put("smsEnabled", false);
        data.put("remark", "当前为兼容联调的最小占位实现，尚未持久化到数据库");
        return Result.success(data);
    }
}
