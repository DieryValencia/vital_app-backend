package com.vitalapp.vital_app_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Errores de validación
    VALIDATION_ERROR("VALIDATION_ERROR", "Error de validación"),

    // Errores de autenticación
    UNAUTHORIZED("UNAUTHORIZED", "No autorizado"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Credenciales inválidas"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token expirado"),
    INVALID_TOKEN("INVALID_TOKEN", "Token inválido"),

    // Errores de recursos
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Recurso no encontrado"),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "Recurso duplicado"),

    // Errores de negocio
    BUSINESS_ERROR("BUSINESS_ERROR", "Error de negocio"),

    // Errores del sistema
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Error interno del servidor"),
    DATABASE_ERROR("DATABASE_ERROR", "Error de base de datos");

    private final String code;
    private final String description;
}