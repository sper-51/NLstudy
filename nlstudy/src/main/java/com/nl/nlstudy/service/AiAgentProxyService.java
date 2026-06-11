package com.nl.nlstudy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nl.nlstudy.config.AiAgentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAgentProxyService {

    private final AiAgentProperties properties;
    private final ObjectMapper objectMapper;

    private HttpClient client() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public Map<String, Object> postJson(String path, Map<String, Object> payload) {
        try {
            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getBaseUrl() + path))
                    .timeout(properties.getTimeout())
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return error("AI服务HTTP " + response.statusCode() + "：" + truncate(response.body()));
            }
            if (response.body() == null || response.body().isBlank()) {
                return error("AI服务返回为空，请确认 learn_agent 已启动并可访问");
            }
            try {
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            } catch (Exception parseException) {
                return error("AI服务返回非JSON：" + truncate(response.body()));
            }
        } catch (Exception e) {
            return error("AI服务调用失败：" + safeMessage(e));
        }
    }

    public Map<String, Object> getJson(String path, Map<String, Object> query) {
        try {
            String url = properties.getBaseUrl() + path + toQueryString(query);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(properties.getTimeout())
                    .GET()
                    .build();
            HttpResponse<String> response = client().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return error("AI服务HTTP " + response.statusCode() + "：" + truncate(response.body()));
            }
            if (response.body() == null || response.body().isBlank()) {
                return error("AI服务返回为空，请确认 learn_agent 已启动并可访问");
            }
            try {
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            } catch (Exception parseException) {
                return error("AI服务返回非JSON：" + truncate(response.body()));
            }
        } catch (Exception e) {
            return error("AI服务调用失败：" + safeMessage(e));
        }
    }

    public Map<String, Object> deleteJson(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getBaseUrl() + path))
                    .timeout(properties.getTimeout())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return error("AI服务HTTP " + response.statusCode() + "：" + truncate(response.body()));
            }
            if (response.body() == null || response.body().isBlank()) {
                return Map.of("success", true);
            }
            try {
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            } catch (Exception parseException) {
                return error("AI服务返回非JSON：" + truncate(response.body()));
            }
        } catch (Exception e) {
            return error("AI服务调用失败：" + safeMessage(e));
        }
    }

    public SseEmitter stream(String path, Map<String, Object> payload) {
        SseEmitter emitter = new SseEmitter(properties.getTimeout().toMillis());
        Thread worker = new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(properties.getBaseUrl() + path))
                        .timeout(properties.getTimeout())
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8))
                        .build();
                HttpResponse<InputStream> response = client().send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    String body = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                    emitter.send(SseEmitter.event().data(Map.of("type", "error", "content", "AI服务HTTP " + response.statusCode() + "：" + truncate(body))));
                    emitter.complete();
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data:")) {
                            emitter.send(SseEmitter.event().data(line.substring(5).trim()));
                        }
                    }
                }
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data(Map.of("type", "error", "content", e.getMessage())));
                } catch (Exception ignored) {
                }
                emitter.completeWithError(e);
            }
        });
        worker.setName("ai-agent-sse-proxy");
        worker.start();
        return emitter;
    }

    private String toQueryString(Map<String, Object> query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        String params = query.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return params.isEmpty() ? "" : "?" + params;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        String compact = value.replaceAll("\\s+", " ").trim();
        return compact.length() > 300 ? compact.substring(0, 300) + "..." : compact;
    }
}
