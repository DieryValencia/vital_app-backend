package com.vitalapp.vital_app_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitalapp.vital_app_backend.dto.appointment.AppointmentCreateDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentResponseDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentUpdateDTO;
import com.vitalapp.vital_app_backend.event.AppointmentCreatedEvent;
import com.vitalapp.vital_app_backend.event.AppointmentStatusChangedEvent;
import com.vitalapp.vital_app_backend.mapper.AppointmentMapper;
import com.vitalapp.vital_app_backend.model.Appointment;
import com.vitalapp.vital_app_backend.model.AppointmentStatus;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.repository.AppointmentRepository;
import com.vitalapp.vital_app_backend.repository.PatientRepository;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Crea una nueva cita
     */
    public AppointmentResponseDTO createAppointment(AppointmentCreateDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + dto.getPatientId()));

        Appointment appointment = appointmentMapper.toEntity(dto);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Publicar evento de creación
        eventPublisher.publishEvent(new AppointmentCreatedEvent(savedAppointment));

        return appointmentMapper.toResponseDTO(savedAppointment);
    }

    /**
     * Obtiene todas las citas
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una cita por ID
     */
    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        return appointmentMapper.toResponseDTO(appointment);
    }

    /**
     * Actualiza una cita
     */
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

        appointmentMapper.updateEntityFromDTO(dto, appointment);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(updatedAppointment);
    }

    /**
     * Elimina una cita
     */
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    /**
     * Obtiene citas por paciente
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene citas por estado
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene citas próximas
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getUpcomingAppointments() {
        return appointmentRepository.findByScheduledAtAfterOrderByScheduledAtAsc(LocalDateTime.now()).stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de una cita
     */
    public AppointmentResponseDTO updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // Publicar evento de cambio de status
        eventPublisher.publishEvent(
            new AppointmentStatusChangedEvent(updatedAppointment, oldStatus, status)
        );

        return appointmentMapper.toResponseDTO(updatedAppointment);
    }

    /**
     * Cancela una cita con razón
     */
    public AppointmentResponseDTO cancelAppointment(Long id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        if (reason != null && !reason.trim().isEmpty()) {
            appointment.setNotes((appointment.getNotes() != null ? appointment.getNotes() + "\n" : "") +
                    "Cancelada: " + reason);
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(updatedAppointment);
    }
}