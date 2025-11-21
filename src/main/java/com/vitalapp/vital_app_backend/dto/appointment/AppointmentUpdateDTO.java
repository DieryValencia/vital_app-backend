package com.vitalapp.vital_app_backend.dto.appointment;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class AppointmentUpdateDTO {

    @Size(max = 100, message = "El nombre del doctor no puede exceder 100 caracteres")
    private String doctor;

    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String specialty;

    @Future(message = "La fecha programada debe ser en el futuro")
    private LocalDateTime scheduledAt;

    @Min(value = 15, message = "La duración debe ser al menos 15 minutos")
    @Max(value = 240, message = "La duración no puede exceder 240 minutos")
    private Integer duration;

    @Size(max = 500, message = "La razón no puede exceder 500 caracteres")
    private String reason;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notes;

    // Getters and setters
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}