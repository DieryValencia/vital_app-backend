package com.vitalapp.vital_app_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Información general de la API
        Info info = new Info()
                .title("VitalApp API")
                .version("1.0.0")
                .description("API REST para sistema de triaje médico y gestión de pacientes. " +
                            "Incluye módulos de autenticación, pacientes, triajes, citas y notificaciones.")
                .contact(new Contact()
                        .name("Equipo VitalApp")
                        .email("contact@vitalapp.com")
                        .url("https://vitalapp.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));

        // Configuración de servidores
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de desarrollo local");

        Server prodServer = new Server()
                .url("https://api.vitalapp.com")
                .description("Servidor de producción");

        // Configuración de seguridad JWT
        String securitySchemeName = "Bearer Authentication";

        SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingrese el token JWT obtenido del endpoint de login. " +
                           "Formato: Bearer {token}");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(securitySchemeName);

        // Componentes (esquemas de seguridad)
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, securityScheme);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, prodServer))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}