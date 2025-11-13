package com.vitalapp.vital_app_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuración de OpenAI para la aplicación VitalApp.
 *
 * Esta clase configura el cliente de OpenAI con la API key y proporciona
 * un bean singleton de OpenAiService para ser inyectado en otros servicios.
 *
 * @author Equipo VitalApp
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class OpenAiConfig {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    /**
     * Crea y configura el bean de OpenAiService.
     * Solo se crea si openai.api.key está configurada.
     *
     * @return OpenAiService configurado con la API key
     */
    @Bean
    @ConditionalOnProperty(name = "openai.api.key")
    public OpenAiService openAiService() {
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
            log.warn("OpenAI API key no está configurada. Características de IA serán deshabilitadas");
            return null;
        }
        log.info("Configurando OpenAI Service con API key");
        return new OpenAiService(openaiApiKey.trim());
    }
}
