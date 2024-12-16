package com.naiomi.employee.api.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management API")
                        .version("1.0.0")
                        .description("API documentation for managing employees")
                        .contact(new Contact()
                                .name("Naiomi Naidoo")
                                .email("naiomi@example.com")
                                .url("https://github.com/C0d3N1nJ4"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Full Documentation")
                        .url("https://example.com/docs"))
                .addServersItem(new Server()
                        .url("http://localhost:9090")
                        .description("Local Development Server"));
    }
}
