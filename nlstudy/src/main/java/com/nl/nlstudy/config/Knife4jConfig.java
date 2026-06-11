package com.nl.nlstudy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能在线考试平台 API")
                        .description("基于SpringBoot的智能在线考试平台接口文档")
                        .version("v1.0")
                        .contact(new Contact().name("NLStudy Team"))
                        .license(new License().name("Apache 2.0")));
    }
}
