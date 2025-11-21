package com.vitalapp.vital_app_backend.dto.appointment;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AppointmentCreateDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long patientId;

    @NotBlank(message = "El nombre del doctor es obligatorio")
    @Size(max = 100, message = "El nombre del doctor no puede exceder 100 caracteres")
    private String doctor;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String specialty;

    @NotNull(message = "La fecha programada es obligatoria")
    @Future(message = "La fecha programada debe ser en el futuro")
    private LocalDateTime scheduledAt;

    @Min(value = 15, message = "La duración debe ser al menos 15 minutos")
    @Max(value = 240, message = "La duración no puede exceder 240 minutos")
    private Integer duration;

    @NotBlank(message = "La razón de la cita es obligatoria")
    @Size(max = 500, message = "La razón no puede exceder 500 caracteres")
    private String reason;

    // Getters and setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}