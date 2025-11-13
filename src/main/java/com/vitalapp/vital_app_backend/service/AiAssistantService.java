package com.vitalapp.vital_app_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para interactuar con OpenAI API.
 *
 * Proporciona métodos para realizar consultas a OpenAI, especialmente
 * para análisis de síntomas de triaje médico en la aplicación VitalApp.
 *
 * @author Equipo VitalApp
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@org.springframework.beans.factory.annotation.Autowired(required = false)})
public class AiAssistantService {

    private final OpenAiService openaiClient;

    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String model;

    @Value("${openai.api.temperature:0.7}")
    private Double temperature;

    @Value("${openai.api.max-tokens:500}")
    private Integer maxTokens;

    private boolean isConfigured() {
        return openaiClient != null;
    }

    /**
     * Analiza síntomas del paciente usando OpenAI.
     *
     * @param symptoms Descripción de los síntomas del paciente
     * @return Análisis de los síntomas generado por OpenAI
     */
    public String analyzeSymptoms(String symptoms) {
        if (!isConfigured()) {
            throw new RuntimeException("OpenAI no está configurado. Establece la variable OPENAI_API_KEY");
        }

        try {
            log.info("Analizando síntomas con OpenAI: {}", symptoms);

            String prompt = "Eres un asistente médico de triaje. Analiza los siguientes síntomas " +
                    "y proporciona una recomendación de urgencia (BAJA, MEDIA, ALTA, CRÍTICA) " +
                    "junto con una breve explicación. Síntomas: " + symptoms;

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

            String response = openaiClient.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.info("Respuesta de OpenAI recibida exitosamente");
            return response;

        } catch (Exception e) {
            log.error("Error al consultar OpenAI: ", e);
            throw new RuntimeException("Error al analizar síntomas con OpenAI: " + e.getMessage());
        }
    }

    /**
     * Genera una recomendación médica basada en síntomas.
     *
     * @param symptoms Descripción de síntomas
     * @param medicalHistory Historial médico relevante (opcional)
     * @return Recomendación generada por OpenAI
     */
    public String generateMedicalRecommendation(String symptoms, String medicalHistory) {
        if (!isConfigured()) {
            throw new RuntimeException("OpenAI no está configurado. Establece la variable OPENAI_API_KEY");
        }

        try {
            log.info("Generando recomendación médica con OpenAI");

            String prompt = "Eres un asistente médico profesional. Basándote en los siguientes síntomas " +
                    "y antecedentes médicos, proporciona una recomendación inicial para el triaje. " +
                    "Síntomas: " + symptoms +
                    (medicalHistory != null ? "\nAntecedentes: " + medicalHistory : "");

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

            String response = openaiClient.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.info("Recomendación generada exitosamente");
            return response;

        } catch (Exception e) {
            log.error("Error al generar recomendación con OpenAI: ", e);
            throw new RuntimeException("Error al generar recomendación: " + e.getMessage());
        }
    }

    /**
     * Realiza una consulta genérica a OpenAI.
     *
     * @param prompt Pregunta o prompt para OpenAI
     * @return Respuesta de OpenAI
     */
    public String chat(String prompt) {
        if (!isConfigured()) {
            throw new RuntimeException("OpenAI no está configurado. Establece la variable OPENAI_API_KEY");
        }

        try {
            log.info("Enviando prompt a OpenAI");

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

            String response = openaiClient.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.info("Respuesta de chat recibida");
            return response;

        } catch (Exception e) {
            log.error("Error en chat con OpenAI: ", e);
            throw new RuntimeException("Error al comunicarse con OpenAI: " + e.getMessage());
        }
    }
}
