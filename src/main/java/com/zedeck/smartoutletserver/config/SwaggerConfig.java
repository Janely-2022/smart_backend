package com.zedeck.smartoutletserver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = { @Server(url = "http://localhost:8089") },
        info = @Info(title = "FEEDBACK PLATFORM API",
                version = "v1",
                description = "FEEDBACK PLATFORM RESTful API",
                license = @License(
                        name = "MIT License"),
                contact = @Contact(url = "https://www.linkedin.com/in/melckzedeck-james-4109031a6?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=ios_app",
                        name = "Melckzedeck James")))
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
}
