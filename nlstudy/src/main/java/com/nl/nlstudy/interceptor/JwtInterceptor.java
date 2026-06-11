package com.nl.nlstudy.interceptor;

import com.nl.nlstudy.util.JwtUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录接口（支持带/api/v1前缀和不带前缀的情况）
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/auth/") || requestURI.startsWith("/api/v1/auth/")) {
            return true;
        }

        // 放行静态资源和Swagger
        if (requestURI.startsWith("/swagger-ui/") || 
            requestURI.startsWith("/v3/api-docs/") || 
            requestURI.startsWith("/doc.html") ||
            requestURI.startsWith("/webjars/")) {
            return true;
        }

        // 获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            // 尝试从X-User-Id获取用户（用于测试）
            String userId = request.getHeader("X-User-Id");
            if (userId != null && !userId.isEmpty()) {
                request.setAttribute("userId", Long.parseLong(userId));
                return true;
            }
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\": 401, \"message\": \"未登录或token已过期\", \"data\": null}");
            return false;
        }

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\": 401, \"message\": \"token无效或已过期\", \"data\": null}");
            return false;
        }

        // 获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        if (userId == null) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\": 401, \"message\": \"token解析失败\", \"data\": null}");
            return false;
        }

        // 注入到请求中
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);
        refreshOnlineUser(userId, request);

        return true;
    }

    private void refreshOnlineUser(Long userId, HttpServletRequest request) {
        try {
            jdbcTemplate.update(
                    "UPDATE online_user SET last_active_time = NOW(), status = 1, ip_address = COALESCE(NULLIF(?, ''), ip_address) WHERE user_id = ?",
                    getClientIp(request),
                    userId
            );
        } catch (Exception ignored) {
        }
    }

    private String getClientIp(HttpServletRequest request) {
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
}
