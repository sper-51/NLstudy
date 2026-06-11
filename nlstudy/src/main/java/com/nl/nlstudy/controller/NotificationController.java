package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Notification;
import com.nl.nlstudy.mapper.NotificationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "消息通知")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationMapper notificationMapper;

    private Long getCurrentUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    @Operation(summary = "获取通知列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            HttpServletRequest request,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("未提供用户ID");
        }

        Page<Notification> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Notification::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        wrapper.orderByDesc(Notification::getCreateTime);
        Page<Notification> result = notificationMapper.selectPage(pageParam, wrapper);
        
        // 统计未读数量
        Long unreadCount = notificationMapper.selectCount(
            new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("pagination", new com.nl.nlstudy.common.PageResult.Pagination(page, pageSize, result.getTotal()));
        response.put("unreadCount", unreadCount);
        return Result.success(response);
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
        return Result.success();
    }

    @Operation(summary = "标记全部已读")
    @PutMapping("/readAll")
    public Result<Void> markAllRead(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("未提供用户ID");
        }

        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
              .eq(Notification::getIsRead, 0)
              .set(Notification::getIsRead, 1)
              .set(Notification::getReadTime, LocalDateTime.now());
        notificationMapper.update(null, wrapper);
        return Result.success();
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        notificationMapper.deleteById(id);
        return Result.success();
    }
}
