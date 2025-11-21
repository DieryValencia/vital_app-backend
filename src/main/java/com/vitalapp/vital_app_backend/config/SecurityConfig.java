package com.vitalapp.vital_app_backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * Configuración de seguridad para la aplicación VitalApp.
 *
 * Esta clase configura Spring Security para implementar autenticación JWT,
 * protección de endpoints y políticas de autorización. Define qué endpoints
 * son públicos (como autenticación y documentación Swagger) y cuáles requieren
 * autenticación previa.
 *
 * @author Equipo VitalApp
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Filtro de autenticación JWT que intercepta las requests para validar tokens.
     * Se ejecuta antes del filtro de autenticación de Spring Security.
     */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Proveedor de autenticación que maneja la lógica de autenticación de usuarios.
     * Utiliza el UserDetailsService y PasswordEncoder configurados.
     */
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configura la cadena de filtros de seguridad de Spring Security.
     *
     * Define las reglas de autorización:
     * - Endpoints de autenticación (/api/auth/**) son públicos
     * - Documentación Swagger es pública
     * - Todos los demás endpoints requieren autenticación JWT
     *
     * También configura:
     * - Deshabilitación de CSRF (no necesario para APIs stateless)
     * - Gestión de sesiones stateless
     * - Filtro JWT en la cadena de filtros
     *
     * @param http Configurador de HttpSecurity para personalizar la seguridad
     * @return SecurityFilterChain configurada
     * @throws Exception Si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})  // Habilitar CORS
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                // Swagger UI y OpenAPI docs (públicos)
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/api-docs/**"
                ).permitAll()

                // Actuator (health checks)
                .requestMatchers("/actuator/**").permitAll()

                // Todos los demás endpoints requieren autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001",
            "https://mid-dianemarie-vitalapp-a45cd570.koyeb.app"
        ));
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}