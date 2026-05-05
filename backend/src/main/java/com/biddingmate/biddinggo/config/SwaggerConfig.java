package com.biddingmate.biddinggo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "bidding-go",
                description = "bidding-go API",
                version = "v1.0.0"
        )
)
public class SwaggerConfig {
    @Value("${OPENAPI_SERVER_URL:http://localhost:8080}")
    private String openApiServerUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(
                        List.of(
                                new Server().url(openApiServerUrl)
                        )
                )
                .components(
                        new Components().addSecuritySchemes(
                                // 보안 스키마의 이름
                                "bearer-auth",
                                // 보안 방식 정의
                                new SecurityScheme()
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .type(SecurityScheme.Type.HTTP)
                        )
                )
                // 보안 방식 적용
                .addSecurityItem(
                        new SecurityRequirement().addList("bearer-auth")
                );
    }
}
