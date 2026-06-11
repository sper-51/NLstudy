package com.nl.nlstudy.service;

import com.nl.nlstudy.config.AiAgentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiAgentProcessManager implements ApplicationRunner {

    private final AiAgentProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isAutoStart()) {
            log.info("AI Agent auto-start disabled");
            return;
        }
        if (isAgentReachable()) {
            log.info("AI Agent already reachable at {}", properties.getBaseUrl());
            return;
        }
        startAgentProcess();
    }

    private boolean isAgentReachable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getBaseUrl() + "/api/sessions"))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 500;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void startAgentProcess() {
        File workDir = new File(properties.getWorkingDirectory());
        File python = new File(properties.getPythonPath());
        File entrypoint = new File(workDir, properties.getEntrypoint());
        if (!workDir.isDirectory() || !python.isFile() || !entrypoint.isFile()) {
            log.warn("AI Agent auto-start skipped, invalid paths. workDir={}, python={}, entrypoint={}",
                    workDir.getAbsolutePath(), python.getAbsolutePath(), entrypoint.getAbsolutePath());
            return;
        }
        try {
            ProcessBuilder builder = new ProcessBuilder(python.getAbsolutePath(), properties.getEntrypoint());
            builder.directory(workDir);
            builder.redirectOutput(new File(workDir, "agent_backend.out.log"));
            builder.redirectError(new File(workDir, "agent_backend.err.log"));
            builder.start();

            for (int i = 0; i < 20; i++) {
                if (isAgentReachable()) {
                    log.info("AI Agent auto-started at {}", properties.getBaseUrl());
                    return;
                }
                TimeUnit.MILLISECONDS.sleep(500);
            }
            log.warn("AI Agent process started but not reachable yet, check logs under {}", workDir.getAbsolutePath());
        } catch (Exception e) {
            log.warn("AI Agent auto-start failed: {}", e.getMessage(), e);
        }
    }
}
