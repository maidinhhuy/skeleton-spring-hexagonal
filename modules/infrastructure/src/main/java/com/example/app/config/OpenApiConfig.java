package com.example.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI tbaOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Spring Hexagonal Skeleton API")
                .version("0.0.1")
                .description(
                    "OpenAPI spec generated from Spring MVC controllers. Authentication uses httpOnly cookies set by the login and refresh endpoints."))
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Local Spring Boot server")))
        .components(
            new Components()
                .addSecuritySchemes(
                    "AccessTokenCookie",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("app_access_token")
                        .description("Access token cookie issued by login and refresh."))
                .addSecuritySchemes(
                    "RefreshTokenCookie",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("app_refresh_token")
                        .description("Refresh token cookie issued by login.")));
  }
}
