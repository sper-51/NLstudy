package com.nl.nlstudy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.agent")
public class AiAgentProperties {
    private String baseUrl = "http://127.0.0.1:5000";
    private Duration timeout = Duration.ofSeconds(120);
    private boolean autoStart = true;
    private String workingDirectory = "D:/123/learn_agent";
    private String pythonPath = "D:/123/learn_agent/venv/Scripts/python.exe";
    private String entrypoint = "app.py";
}
