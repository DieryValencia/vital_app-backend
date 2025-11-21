package com.vitalapp.vital_app_backend.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vitalapp.vital_app_backend.dto.patient.PatientCreateDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientUpdateDTO;
import com.vitalapp.vital_app_backend.exception.custom.DuplicateResourceException;
import com.vitalapp.vital_app_backend.exception.custom.ResourceNotFoundException;
import com.vitalapp.vital_app_backend.mapper.PatientMapper;
import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Service Tests")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientCreateDTO createDTO;
    private PatientResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .fullName("Juan Pérez")
                .documentNumber("1234567890")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .active(true)
                .build();

        createDTO = PatientCreateDTO.builder()
                .fullName("Juan Pérez")
                .documentNumber("1234567890")
                .birthDate(LocalDate.of(1990, 5, 15))
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
    @DisplayName("Debe crear paciente exitosamente")
    void createPatient_shouldCreateSuccessfully() {
        // Given
        when(patientRepository.existsByDocumentNumber(anyString())).thenReturn(false);
        when(patientMapper.toEntity(any(PatientCreateDTO.class))).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(responseDTO);

        // When
        PatientResponseDTO result = patientService.createPatient(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("Juan Pérez", result.getFullName());
        verify(patientRepository, times(1)).existsByDocumentNumber(anyString());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si documento ya existe")
    void createPatient_shouldThrowExceptionWhenDocumentExists() {
        // Given
        when(patientRepository.existsByDocumentNumber(anyString())).thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> patientService.createPatient(createDTO)
        );

        assertThat(exception.getMessage()).contains("Ya existe un paciente");
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debe obtener paciente por ID")
    void getPatientById_shouldReturnPatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(responseDTO);

        // When
        PatientResponseDTO result = patientService.getPatientById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si paciente no existe por ID")
    void getPatientById_shouldThrowExceptionWhenNotFound() {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> patientService.getPatientById(999L)
        );

        assertThat(exception.getMessage()).contains("Paciente no encontrado");
    }

    @Test
    @DisplayName("Debe obtener paciente por documento")
    void getPatientByDocument_shouldReturnPatient() {
        // Given
        when(patientRepository.findByDocumentNumber("1234567890")).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(responseDTO);

        // When
        PatientResponseDTO result = patientService.getPatientByDocument("1234567890");

        // Then
        assertNotNull(result);
        assertEquals("1234567890", result.getDocumentNumber());
        verify(patientRepository, times(1)).findByDocumentNumber("1234567890");
    }

    @Test
    @DisplayName("Debe lanzar excepción si paciente no existe por documento")
    void getPatientByDocument_shouldThrowExceptionWhenNotFound() {
        // Given
        when(patientRepository.findByDocumentNumber("9999999999")).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> patientService.getPatientByDocument("9999999999")
        );

        assertThat(exception.getMessage()).contains("Paciente no encontrado");
    }

    @Test
    @DisplayName("Debe obtener pacientes activos")
    void getActivePatients_shouldReturnActivePatients() {
        // Given
        List<Patient> patients = Arrays.asList(patient);
        when(patientRepository.findByActiveTrue()).thenReturn(patients);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(responseDTO);

        // When
        List<PatientResponseDTO> result = patientService.getActivePatients();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Debe buscar pacientes por nombre")
    void searchPatientsByName_shouldReturnMatchingPatients() {
        // Given
        List<Patient> patients = Arrays.asList(patient);
        when(patientRepository.findByFullNameContainingIgnoreCase("Juan")).thenReturn(patients);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(responseDTO);

        // When
        List<PatientResponseDTO> result = patientService.searchPatientsByName("Juan");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByFullNameContainingIgnoreCase("Juan");
    }

    @Test
    @DisplayName("Debe actualizar paciente exitosamente")
    void updatePatient_shouldUpdateSuccessfully() {
        // Given
        PatientUpdateDTO updateDTO = PatientUpdateDTO.builder()
                .fullName("Juan Pérez Actualizado")
                .phone("+573001111111")
                .build();

        Patient updatedPatient = Patient.builder()
                .id(1L)
                .fullName("Juan Pérez Actualizado")
                .documentNumber("1234567890")
                .phone("+573001111111")
                .active(true)
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(responseDTO);

        // When
        PatientResponseDTO result = patientService.updatePatient(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository, times(1)).findById(1L);
        verify(patientMapper, times(1)).updateEntityFromDTO(any(PatientUpdateDTO.class), any(Patient.class));
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar paciente inexistente")
    void updatePatient_shouldThrowExceptionWhenNotFound() {
        // Given
        PatientUpdateDTO updateDTO = PatientUpdateDTO.builder()
                .fullName("Nuevo Nombre")
                .build();

        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> patientService.updatePatient(999L, updateDTO)
        );

        assertThat(exception.getMessage()).contains("Paciente no encontrado");
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debe desactivar paciente exitosamente")
    void deactivatePatient_shouldDeactivateSuccessfully() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        patientService.deactivatePatient(1L);

        // Then
        assertFalse(patient.isActive());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al desactivar paciente inexistente")
    void deactivatePatient_shouldThrowExceptionWhenNotFound() {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> patientService.deactivatePatient(999L)
        );

        assertThat(exception.getMessage()).contains("Paciente no encontrado");
    }

    @Test
    @DisplayName("Debe eliminar paciente exitosamente")
    void deletePatient_shouldDeleteSuccessfully() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        // When
        patientService.deletePatient(1L);

        // Then
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar paciente inexistente")
    void deletePatient_shouldThrowExceptionWhenNotFound() {
        // Given
        when(patientRepository.existsById(999L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> patientService.deletePatient(999L)
        );

        assertThat(exception.getMessage()).contains("Paciente no encontrado");
        verify(patientRepository, never()).deleteById(anyLong());
    }
}