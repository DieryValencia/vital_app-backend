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

import com.vitalapp.vital_app_backend.dto.appointment.AppointmentCreateDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentResponseDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentUpdateDTO;
import com.vitalapp.vital_app_backend.mapper.AppointmentMapper;
import com.vitalapp.vital_app_backend.model.Appointment;
import com.vitalapp.vital_app_backend.model.AppointmentStatus;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.repository.AppointmentRepository;
import com.vitalapp.vital_app_backend.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appointment Service Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment;
    private Patient patient;
    private AppointmentCreateDTO createDTO;
    private AppointmentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .fullName("Juan Pérez")
                .active(true)
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor("Dr. García")
                .specialty("Medicina General")
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .duration(30)
                .status(AppointmentStatus.SCHEDULED)
                .reason("Consulta general")
                .notes("Paciente estable")
                .createdAt(LocalDateTime.now())
                .build();

        createDTO = new AppointmentCreateDTO();
        createDTO.setPatientId(1L);
        createDTO.setDoctor("Dr. García");
        createDTO.setSpecialty("Medicina General");
        createDTO.setScheduledAt(LocalDateTime.now().plusDays(1));
        createDTO.setDuration(30);
        createDTO.setReason("Consulta general");

        responseDTO = new AppointmentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setPatientId(1L);
        responseDTO.setPatientName("Juan Pérez");
        responseDTO.setDoctor("Dr. García");
        responseDTO.setSpecialty("Medicina General");
        responseDTO.setScheduledAt(LocalDateTime.now().plusDays(1));
        responseDTO.setDuration(30);
        responseDTO.setStatus(AppointmentStatus.SCHEDULED);
        responseDTO.setReason("Consulta general");
        responseDTO.setNotes("Paciente estable");
        responseDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debe crear cita exitosamente")
    void createAppointment_shouldCreateSuccessfully() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentMapper.toEntity(any(AppointmentCreateDTO.class))).thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        AppointmentResponseDTO result = appointmentService.createAppointment(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("Dr. García", result.getDoctor());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si paciente no existe")
    void createAppointment_shouldThrowExceptionWhenPatientNotFound() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> appointmentService.createAppointment(createDTO)
        );

        assertThat(exception.getMessage()).contains("Paciente no encontrado");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe obtener cita por ID")
    void getAppointmentById_shouldReturnAppointment() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        AppointmentResponseDTO result = appointmentService.getAppointmentById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan Pérez", result.getPatientName());
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si cita no existe por ID")
    void getAppointmentById_shouldThrowExceptionWhenNotFound() {
        // Given
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> appointmentService.getAppointmentById(999L)
        );

        assertThat(exception.getMessage()).contains("Cita no encontrada");
    }

    @Test
    @DisplayName("Debe obtener todas las citas")
    void getAllAppointments_shouldReturnList() {
        // Given
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        List<AppointmentResponseDTO> result = appointmentService.getAllAppointments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener citas por paciente")
    void getAppointmentsByPatient_shouldReturnList() {
        // Given
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByPatientId(1L)).thenReturn(appointments);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        List<AppointmentResponseDTO> result = appointmentService.getAppointmentsByPatient(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByPatientId(1L);
    }

    @Test
    @DisplayName("Debe obtener citas por estado")
    void getAppointmentsByStatus_shouldReturnList() {
        // Given
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED)).thenReturn(appointments);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        List<AppointmentResponseDTO> result = appointmentService.getAppointmentsByStatus(AppointmentStatus.SCHEDULED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Debe obtener citas próximas")
    void getUpcomingAppointments_shouldReturnUpcoming() {
        // Given
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByScheduledAtAfterOrderByScheduledAtAsc(any(LocalDateTime.class))).thenReturn(appointments);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        List<AppointmentResponseDTO> result = appointmentService.getUpcomingAppointments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByScheduledAtAfterOrderByScheduledAtAsc(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe actualizar cita exitosamente")
    void updateAppointment_shouldUpdateSuccessfully() {
        // Given
        AppointmentUpdateDTO updateDTO = new AppointmentUpdateDTO();
        updateDTO.setDoctor("Dr. López");
        updateDTO.setSpecialty("Cardiología");
        updateDTO.setDuration(45);
        updateDTO.setReason("Consulta especializada");
        updateDTO.setNotes("Notas actualizadas");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        AppointmentResponseDTO result = appointmentService.updateAppointment(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentMapper, times(1)).updateEntityFromDTO(any(AppointmentUpdateDTO.class), any(Appointment.class));
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe actualizar estado de cita")
    void updateAppointmentStatus_shouldUpdateStatus() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        AppointmentResponseDTO result = appointmentService.updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);

        // Then
        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe cancelar cita con razón")
    void cancelAppointment_shouldCancelWithReason() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(responseDTO);

        // When
        AppointmentResponseDTO result = appointmentService.cancelAppointment(1L, "Paciente no puede asistir");

        // Then
        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe eliminar cita exitosamente")
    void deleteAppointment_shouldDeleteSuccessfully() {
        // Given
        when(appointmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(1L);

        // When
        appointmentService.deleteAppointment(1L);

        // Then
        verify(appointmentRepository, times(1)).existsById(1L);
        verify(appointmentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cita inexistente")
    void deleteAppointment_shouldThrowExceptionWhenNotFound() {
        // Given
        when(appointmentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> appointmentService.deleteAppointment(999L)
        );

        assertThat(exception.getMessage()).contains("Cita no encontrada");
        verify(appointmentRepository, never()).deleteById(anyLong());
    }
}