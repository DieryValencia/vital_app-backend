package com.vitalapp.vital_app_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vitalapp.vital_app_backend.model.Appointment;
import com.vitalapp.vital_app_backend.model.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Busca citas por paciente
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Busca citas por estado
     */
    List<Appointment> findByStatus(AppointmentStatus status);

    /**
     * Busca citas entre fechas
     */
    List<Appointment> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Busca citas por paciente y estado
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    /**
     * Busca citas programadas después de una fecha ordenadas por fecha ascendente
     */
    List<Appointment> findByScheduledAtAfterOrderByScheduledAtAsc(LocalDateTime date);
}