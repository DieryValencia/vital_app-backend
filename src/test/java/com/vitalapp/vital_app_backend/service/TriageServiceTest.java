package com.vitalapp.vital_app_backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.vitalapp.vital_app_backend.dto.triage.TriageCreateDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageResponseDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageUpdateDTO;
import com.vitalapp.vital_app_backend.mapper.TriageMapper;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.model.Triage;
import com.vitalapp.vital_app_backend.model.TriageStatus;
import com.vitalapp.vital_app_backend.repository.PatientRepository;
import com.vitalapp.vital_app_backend.repository.TriageRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Triage Service Tests")
class TriageServiceTest {

    @Mock
    private TriageRepository triageRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private TriageMapper triageMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TriageService triageService;

    private Triage triage;
    private Patient patient;
    private TriageCreateDTO createDTO;
    private TriageResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .fullName("Juan Pérez")
                .active(true)
                .build();

        triage = Triage.builder()
                .id(1L)
                .patient(patient)
                .symptoms("Dolor de cabeza intenso")
                .bloodPressure("120/80")
                .heartRate(80)
                .temperature(36.5)
                .oxygenSaturation(98)
                .severityLevel(3)
                .recommendedAction("Reposo y analgésicos")
                .status(TriageStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .notes("Paciente estable")
                .build();

        createDTO = new TriageCreateDTO();
        createDTO.setPatientId(1L);
        createDTO.setSymptoms("Dolor de cabeza intenso");
        createDTO.setBloodPressure("120/80");
        createDTO.setHeartRate(80);
        createDTO.setTemperature(36.5);
        createDTO.setOxygenSaturation(98);
        createDTO.setSeverityLevel(3);
        createDTO.setRecommendedAction("Reposo y analgésicos");

        responseDTO = new TriageResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setPatientId(1L);
        responseDTO.setPatientName("Juan Pérez");
        responseDTO.setSymptoms("Dolor de cabeza intenso");
        responseDTO.setBloodPressure("120/80");
        responseDTO.setHeartRate(80);
        responseDTO.setTemperature(36.5);
        responseDTO.setOxygenSaturation(98);
        responseDTO.setSeverityLevel(3);
        responseDTO.setRecommendedAction("Reposo y analgésicos");
        responseDTO.setStatus(TriageStatus.PENDING);
        responseDTO.setCreatedAt(LocalDateTime.now());
        responseDTO.setNotes("Paciente estable");
    }

    @Test
    @DisplayName("Debe crear triage exitosamente")
    void createTriage_shouldCreateSuccessfully() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(triageMapper.toEntity(any(TriageCreateDTO.class))).thenReturn(triage);
        when(triageRepository.save(any(Triage.class))).thenReturn(triage);
        when(triageMapper.toResponseDTO(any(Triage.class))).thenReturn(responseDTO);

        // When
        TriageResponseDTO result = triageService.createTriage(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("Dolor de cabeza intenso", result.getSymptoms());
        assertEquals(TriageStatus.PENDING, result.getStatus());
        verify(triageRepository, times(1)).save(any(Triage.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si paciente no existe")
    void createTriage_shouldThrowExceptionWhenPatientNotFound() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> triageService.createTriage(createDTO)
        );

        assertThat(exception.getMessage()).contains("Paciente no encontrado");
        verify(triageRepository, never()).save(any(Triage.class));
    }

    @Test
    @DisplayName("Debe obtener triage por ID")
    void getTriageById_shouldReturnTriage() {
        // Given
        when(triageRepository.findById(1L)).thenReturn(Optional.of(triage));
        when(triageMapper.toResponseDTO(any(Triage.class))).thenReturn(responseDTO);

        // When
        TriageResponseDTO result = triageService.getTriageById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan Pérez", result.getPatientName());
        verify(triageRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si triage no existe por ID")
    void getTriageById_shouldThrowExceptionWhenNotFound() {
        // Given
        when(triageRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> triageService.getTriageById(999L)
        );

        assertThat(exception.getMessage()).contains("Triage no encontrado");
    }

    @Test
    @DisplayName("Debe obtener todos los triages")
    void getAllTriages_shouldReturnList() {
        // Given
        List<Triage> triages = Arrays.asList(triage);
        when(triageRepository.findAll()).thenReturn(triages);
        when(triageMapper.toResponseDTO(any(Triage.class))).thenReturn(responseDTO);

        // When
        List<TriageResponseDTO> result = triageService.getAllTriages();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(triageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener triages por paciente")
    void getTriagesByPatient_shouldReturnList() {
        // Given
        List<Triage> triages = Arrays.asList(triage);
        when(triageRepository.findByPatientId(1L)).thenReturn(triages);
        when(triageMapper.toResponseDTO(any(Triage.class))).thenReturn(responseDTO);

        // When
        List<TriageResponseDTO> result = triageService.getTriagesByPatient(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(triageRepository, times(1)).findByPatientId(1L);
    }

    @Test
    @DisplayName("Debe obtener triages por estado")
    void getTriagesByStatus_shouldReturnList() {
        // Given
        List<Triage> triages = Arrays.asList(triage);
        when(triageRepository.findByStatus(TriageStatus.PENDING)).thenReturn(triages);
        when(triageMapper.toResponseDTO(any(Triage.class))).thenReturn(responseDTO);

        // When
        List<TriageResponseDTO> result = triageService.getTriagesByStatus(TriageStatus.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(triageRepository, times(1)).findByStatus(TriageStatus.PENDING);
    }

    @Test
    @DisplayName("Debe actualizar triage exitosamente")
    void updateTriage_shouldUpdateSuccessfully() {
        // Given
        TriageUpdateDTO updateDTO = new TriageUpdateDTO();
        updateDTO.setBloodPressure("130/85");
        updateDTO.setHeartRate(85);
        updateDTO.setTemperature(37.0);
        updateDTO.setOxygenSaturation(97);
        updateDTO.setRecommendedAction("Acción actualizada");
        updateDTO.setNotes("Notas actualizadas");

        when(triageRepository.findById(1L)).thenReturn(Optional.of(triage));
        when(triageRepository.save(any(Triage.class))).thenReturn(triage);
        when(triageMapper.toResponseDTO(any(Triage.class))).thenReturn(responseDTO);

        // When
        TriageResponseDTO result = triageService.updateTriage(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(triageRepository, times(1)).findById(1L);
        verify(triageMapper, times(1)).updateEntityFromDTO(any(TriageUpdateDTO.class), any(Triage.class));
        verify(triageRepository, times(1)).save(any(Triage.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar triage inexistente")
    void updateTriage_shouldThrowExceptionWhenNotFound() {
        // Given
        TriageUpdateDTO updateDTO = new TriageUpdateDTO();
        updateDTO.setNotes("Notas");

        when(triageRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> triageService.updateTriage(999L, updateDTO)
        );

        assertThat(exception.getMessage()).contains("Triage no encontrado");
        verify(triageRepository, never()).save(any(Triage.class));
    }

    @Test
    @DisplayName("Debe actualizar estado del triage")
    void updateTriageStatus_shouldUpdateStatus() {
        // Given
        when(triageRepository.findById(1L)).thenReturn(Optional.of(triage));
        when(triageRepository.save(any(Triage.class))).thenReturn(triage);
        when(triageMapper.toResponseDTO(any(Triage.class))).thenReturn(responseDTO);

        // When
        TriageResponseDTO result = triageService.updateTriageStatus(1L, TriageStatus.COMPLETED);

        // Then
        assertNotNull(result);
        verify(triageRepository, times(1)).findById(1L);
        verify(triageRepository, times(1)).save(any(Triage.class));
    }

    @Test
    @DisplayName("Debe eliminar triage exitosamente")
    void deleteTriage_shouldDeleteSuccessfully() {
        // Given
        when(triageRepository.existsById(1L)).thenReturn(true);
        doNothing().when(triageRepository).deleteById(1L);

        // When
        triageService.deleteTriage(1L);

        // Then
        verify(triageRepository, times(1)).existsById(1L);
        verify(triageRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar triage inexistente")
    void deleteTriage_shouldThrowExceptionWhenNotFound() {
        // Given
        when(triageRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> triageService.deleteTriage(999L)
        );

        assertThat(exception.getMessage()).contains("Triage no encontrado");
        verify(triageRepository, never()).deleteById(anyLong());
    }
}