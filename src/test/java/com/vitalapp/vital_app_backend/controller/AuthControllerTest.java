package com.vitalapp.vital_app_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitalapp.vital_app_backend.dto.auth.AuthResponseDTO;
import com.vitalapp.vital_app_backend.dto.auth.LoginRequestDTO;
import com.vitalapp.vital_app_backend.dto.auth.RefreshTokenRequestDTO;
import com.vitalapp.vital_app_backend.dto.auth.RegisterRequestDTO;
import com.vitalapp.vital_app_backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Auth Controller Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private RefreshTokenRequestDTO refreshDTO;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        registerDTO = RegisterRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        loginDTO = LoginRequestDTO.builder()
                .username("testuser")
                .password("password123")
                .build();

        refreshDTO = RefreshTokenRequestDTO.builder()
                .refreshToken("eyJhbGciOiJIUzI1NiJ9...")
                .build();

        authResponse = AuthResponseDTO.builder()
                .token("eyJhbGciOiJIUzI1NiJ9...")
                .refreshToken("eyJhbGciOiJIUzI1NiJ9...")
                .type("Bearer")
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("USER")
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register debe registrar usuario")
    void register_shouldReturnToken() throws Exception {
        // Given
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.type", is("Bearer")));
    }

    @Test
    @DisplayName("POST /api/auth/login debe retornar token")
    void login_shouldReturnToken() throws Exception {
        // Given
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/refresh debe renovar token")
    void refreshToken_shouldReturnNewToken() throws Exception {
        // Given
        when(authService.refreshToken(any(RefreshTokenRequestDTO.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/register con datos inválidos debe retornar 400")
    void register_withInvalidData_shouldReturn400() throws Exception {
        // Given
        RegisterRequestDTO invalidDTO = RegisterRequestDTO.builder()
                .username("ab") // Muy corto
                .email("invalid-email")
                .password("123") // Muy corta
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login con datos inválidos debe retornar 400")
    void login_withInvalidData_shouldReturn400() throws Exception {
        // Given
        LoginRequestDTO invalidDTO = LoginRequestDTO.builder()
                .username("") // Vacío
                .password("") // Vacío
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/refresh con refresh token inválido debe retornar 400")
    void refreshToken_withInvalidToken_shouldReturn400() throws Exception {
        // Given
        RefreshTokenRequestDTO invalidDTO = RefreshTokenRequestDTO.builder()
                .refreshToken("") // Vacío
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register debe incluir refresh token en respuesta")
    void register_shouldIncludeRefreshToken() throws Exception {
        // Given
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    @DisplayName("POST /api/auth/login debe incluir rol en respuesta")
    void login_shouldIncludeRole() throws Exception {
        // Given
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("USER")));
    }
}