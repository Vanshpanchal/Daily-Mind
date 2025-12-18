package spring.project.Daily.Mind.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Daily-Mind API",
                version = "1.0",
                description = "API for the Daily-Mind journaling application",
                license = @License(name = "Apache 1.0")
        ),
        tags = {
                @Tag(name = "User", description = "Operations related to the authenticated user"),
                @Tag(name = "Journal", description = "Create, read, update and delete journal entries"),
                @Tag(name = "Admin", description = "Administrative operations"),
                @Tag(name = "Public", description = "Authentication and public endpoints"),
                @Tag(name = "Utility", description = "Utility and maintenance endpoints")
        },
        security = {@SecurityRequirement(name = "BearerAuth")} // Apply globally so protected endpoints inherit the requirement
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
    // Intentionally left blank - metadata is provided through annotations above.
}
