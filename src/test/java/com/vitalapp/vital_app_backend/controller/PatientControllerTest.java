package com.vitalapp.vital_app_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitalapp.vital_app_backend.dto.patient.PatientCreateDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientUpdateDTO;
import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Patient Controller Integration Tests")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    private PatientCreateDTO createDTO;
    private PatientResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createDTO = PatientCreateDTO.builder()
                .fullName("Juan Pérez")
                .documentNumber("1234567890")
                .birthDate(LocalDate.of(1990, 5, 15))
                .phone("+573001234567")
                .gender(Gender.MALE)
                .build();

        responseDTO = PatientResponseDTO.builder()
                .id(1L)
                .fullName("Juan Pérez")
                .documentNumber("1234567890")
                .age(34)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("GET /api/patients sin autenticación debe retornar 403")
    void getAllPatients_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/patients con autenticación debe retornar 200")
    void getAllPatients_withAuth_shouldReturn200() throws Exception {
        // Given
        List<PatientResponseDTO> patients = Arrays.asList(responseDTO);
        when(patientService.getAllPatients(anyInt(), anyInt(), anyString(), anyString(),
                any(), any(), any(), any(), any(), any(), any())).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/patients/active debe retornar pacientes activos")
    void getActivePatients_shouldReturnActivePatients() throws Exception {
        // Given
        List<PatientResponseDTO> patients = Arrays.asList(responseDTO);
        when(patientService.getActivePatients()).thenReturn(patients);

        // When & Then
        mockMvc.perform(get("/api/patients/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("Juan Pérez")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/patients/{id} debe retornar paciente")
    void getPatientById_shouldReturnPatient() throws Exception {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Juan Pérez")))
                .andExpect(jsonPath("$.age", is(34)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/patients/document/{documentNumber} debe retornar paciente")
    void getPatientByDocument_shouldReturnPatient() throws Exception {
        // Given
        when(patientService.getPatientByDocument("1234567890")).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/patients/document/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentNumber", is("1234567890")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/patients/search debe retornar resultados de búsqueda")
    void searchPatients_shouldReturnSearchResults() throws Exception {
        // Given
        List<PatientResponseDTO> patients = Arrays.asList(responseDTO);
        when(patientService.searchPatientsByName("Juan")).thenReturn(patients);

        // When & Then
        mockMvc.perform(get("/api/patients/search").param("name", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("Juan Pérez")));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/patients debe crear paciente")
    void createPatient_shouldCreateAndReturn201() throws Exception {
        // Given
        when(patientService.createPatient(any(PatientCreateDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Juan Pérez")));

        verify(patientService, times(1)).createPatient(any(PatientCreateDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/patients con datos inválidos debe retornar 400")
    void createPatient_withInvalidData_shouldReturn400() throws Exception {
        // Given
        PatientCreateDTO invalidDTO = PatientCreateDTO.builder()
                .fullName("AB") // Muy corto
                .documentNumber("123") // Muy corto
                .build();

        // When & Then
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/patients/{id} debe actualizar paciente")
    void updatePatient_shouldUpdateAndReturn200() throws Exception {
        // Given
        PatientUpdateDTO updateDTO = PatientUpdateDTO.builder()
                .fullName("Juan Pérez Actualizado")
                .phone("+573001111111")
                .build();

        PatientResponseDTO updatedResponse = PatientResponseDTO.builder()
                .id(1L)
                .fullName("Juan Pérez Actualizado")
                .documentNumber("1234567890")
                .phone("+573001111111")
                .age(34)
                .active(true)
                .build();

        when(patientService.updatePatient(anyLong(), any(PatientUpdateDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Juan Pérez Actualizado")))
                .andExpect(jsonPath("$.phone", is("+573001111111")));

        verify(patientService, times(1)).updatePatient(anyLong(), any(PatientUpdateDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/patients/{id}/deactivate debe desactivar paciente")
    void deactivatePatient_shouldReturn204() throws Exception {
        // Given
        doNothing().when(patientService).deactivatePatient(anyLong());

        // When & Then
        mockMvc.perform(patch("/api/patients/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deactivatePatient(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/patients/{id} debe eliminar paciente")
    void deletePatient_shouldReturn204() throws Exception {
        // Given
        doNothing().when(patientService).deletePatient(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/patients con parámetros de paginación debe funcionar")
    void getAllPatients_withPaginationParams_shouldWork() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/patients")
                .param("page", "1")
                .param("size", "5")
                .param("sortBy", "fullName")
                .param("sortDirection", "DESC")
                .param("fullName", "Juan")
                .param("active", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/patients con filtros de fecha debe funcionar")
    void getAllPatients_withDateFilters_shouldWork() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/patients")
                .param("birthDateFrom", "1990-01-01")
                .param("birthDateTo", "2000-12-31"))
                .andExpect(status().isOk());
    }
}