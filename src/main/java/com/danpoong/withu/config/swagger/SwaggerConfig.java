package com.danpoong.withu.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

// 스웨거 접속 주소
// http://localhost:8080/swagger-ui/index.html#/

@Configuration
public class SwaggerConfig {
    @Value("${api.server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url(serverUrl));
    }

    Info info = new Info().title("WITHU Backend APIS").version("0.0.1").description(
            "<h3>WITHU Backend APIS</h3>");

}
