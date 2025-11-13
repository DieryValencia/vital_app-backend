package com.vitalapp.vital_app_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalapp.vital_app_backend.service.AiAssistantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador para operaciones de IA/OpenAI.
 *
 * Proporciona endpoints para interactuar con OpenAI para análisis de síntomas
 * y generación de recomendaciones médicas en el proceso de triaje.
 *
 * @author Equipo VitalApp
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "Endpoints para interacciones con OpenAI")
@ConditionalOnProperty(name = "openai.api.key")
public class AiController {

    private final AiAssistantService aiService;

    public AiController(@Autowired(required = false) AiAssistantService aiService) {
        this.aiService = aiService;
    }

    /**
     * Analiza síntomas de un paciente usando OpenAI.
     *
     * @param request DTO con los síntomas del paciente
     * @return Análisis de síntomas de OpenAI
     */
    @PostMapping("/analyze-symptoms")
    @Operation(summary = "Analizar síntomas",
            description = "Usa OpenAI para analizar síntomas y proporcionar nivel de urgencia")
    public ResponseEntity<?> analyzeSymptoms(@RequestBody SymptomsRequest request) {
        if (aiService == null) {
            log.warn("OpenAI no está configurado");
            return ResponseEntity.status(503)
                    .body(Map.of("success", false, "message", "OpenAI no está configurado. Configura OPENAI_API_KEY en variables de entorno.", "data", null));
        }
        try {
            log.info("Analizando síntomas para paciente");
            String analysis = aiService.analyzeSymptoms(request.getSymptoms());
            return ResponseEntity.ok(Map.of("success", true, "message", "Análisis completado", "data", analysis));
        } catch (Exception e) {
            log.error("Error al analizar síntomas: ", e);
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage(), "data", null));
        }
    }

    /**
     * Genera una recomendación médica basada en síntomas y antecedentes.
     *
     * @param request DTO con síntomas e historial médico
     * @return Recomendación médica generada por OpenAI
     */
    @PostMapping("/generate-recommendation")
    @Operation(summary = "Generar recomendación médica",
            description = "Usa OpenAI para generar una recomendación de triaje")
    public ResponseEntity<?> generateRecommendation(
            @RequestBody RecommendationRequest request) {
        if (aiService == null) {
            log.warn("OpenAI no está configurado");
            return ResponseEntity.status(503)
                    .body(Map.of("success", false, "message", "OpenAI no está configurado. Configura OPENAI_API_KEY en variables de entorno.", "data", null));
        }
        try {
            log.info("Generando recomendación médica");
            String recommendation = aiService.generateMedicalRecommendation(
                    request.getSymptoms(),
                    request.getMedicalHistory());
            return ResponseEntity.ok(Map.of("success", true, "message", "Recomendación generada", "data", recommendation));
        } catch (Exception e) {
            log.error("Error al generar recomendación: ", e);
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage(), "data", null));
        }
    }

    /**
     * Realiza una consulta genérica a OpenAI (chat).
     *
     * @param request DTO con el mensaje/prompt
     * @return Respuesta de OpenAI
     */
    @PostMapping("/chat")
    @Operation(summary = "Chat con IA",
            description = "Realiza una consulta genérica a OpenAI")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        if (aiService == null) {
            log.warn("OpenAI no está configurado");
            return ResponseEntity.status(503)
                    .body(Map.of("success", false, "message", "OpenAI no está configurado. Configura OPENAI_API_KEY en variables de entorno.", "data", null));
        }
        try {
            log.info("Chat iniciado con OpenAI");
            String response = aiService.chat(request.getMessage());
            return ResponseEntity.ok(Map.of("success", true, "message", "Chat completado", "data", response));
        } catch (Exception e) {
            log.error("Error en chat: ", e);
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage(), "data", null));
        }
    }

    // ========== DTOs ==========

    /**
     * DTO para solicitud de análisis de síntomas.
     */
    public static class SymptomsRequest {
        private String symptoms;

        public SymptomsRequest() {}
        public SymptomsRequest(String symptoms) {
            this.symptoms = symptoms;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }
    }

    /**
     * DTO para respuesta de análisis de síntomas.
     */
    public static class SymptomsAnalysisResponse {
        private String analysis;

        public SymptomsAnalysisResponse() {}
        public SymptomsAnalysisResponse(String analysis) {
            this.analysis = analysis;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }
    }

    /**
     * DTO para solicitud de recomendación médica.
     */
    public static class RecommendationRequest {
        private String symptoms;
        private String medicalHistory;

        public RecommendationRequest() {}
        public RecommendationRequest(String symptoms, String medicalHistory) {
            this.symptoms = symptoms;
            this.medicalHistory = medicalHistory;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }

        public String getMedicalHistory() {
            return medicalHistory;
        }

        public void setMedicalHistory(String medicalHistory) {
            this.medicalHistory = medicalHistory;
        }
    }

    /**
     * DTO para respuesta de recomendación médica.
     */
    public static class RecommendationResponse {
        private String recommendation;

        public RecommendationResponse() {}
        public RecommendationResponse(String recommendation) {
            this.recommendation = recommendation;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }

    /**
     * DTO para solicitud de chat.
     */
    public static class ChatRequest {
        private String message;

        public ChatRequest() {}
        public ChatRequest(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * DTO para respuesta de chat.
     */
    public static class ChatResponse {
        private String response;

        public ChatResponse() {}
        public ChatResponse(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }
}
