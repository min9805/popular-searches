package com.example.search.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${version}")
    private String version;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addServersItem(new Server().url("/"))
                .components(new Components().addSecuritySchemes("access-token",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(apiInfo())
                .tags(tagList());
    }

    private Info apiInfo() {
        return new Info()
                .title("Search-Rank")
                .description("실시간 검색어 순위 API")
                .version(version);
    }

    private List<Tag> tagList() {
        return List.of(
                new Tag().name("Search").description("Search API")
        );
    }
}