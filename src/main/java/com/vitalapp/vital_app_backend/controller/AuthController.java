package com.vitalapp.vital_app_backend.controller;

import com.vitalapp.vital_app_backend.dto.auth.*;
import com.vitalapp.vital_app_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticación y registro de usuarios")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una cuenta de usuario nueva en el sistema. No requiere autenticación."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o usuario/email ya existe"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve tokens JWT (access token y refresh token)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Renovar token de acceso",
        description = "Genera un nuevo access token usando el refresh token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token renovado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token inválido o expirado"
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        AuthResponseDTO response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}