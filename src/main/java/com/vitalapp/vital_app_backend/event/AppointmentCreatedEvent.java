package com.vitalapp.vital_app_backend.event;

import com.vitalapp.vital_app_backend.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AppointmentCreatedEvent {
    private final Appointment appointment;
    private final Long patientId;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime timestamp;

    // Constructor adicional que recibe solo Appointment
    public AppointmentCreatedEvent(Appointment appointment) {
        this.appointment = appointment;
        this.patientId = appointment.getPatient() != null ? appointment.getPatient().getId() : null;
        this.scheduledAt = appointment.getScheduledAt();
        this.timestamp = LocalDateTime.now();
    }
}