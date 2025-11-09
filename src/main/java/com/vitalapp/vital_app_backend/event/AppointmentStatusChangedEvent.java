package com.vitalapp.vital_app_backend.event;

import com.vitalapp.vital_app_backend.model.Appointment;
import com.vitalapp.vital_app_backend.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AppointmentStatusChangedEvent {
    private final Appointment appointment;
    private final AppointmentStatus oldStatus;
    private final AppointmentStatus newStatus;
    private final Long patientId;
    private final LocalDateTime timestamp;

    // Constructor adicional que recibe Appointment, oldStatus, newStatus
    public AppointmentStatusChangedEvent(Appointment appointment, AppointmentStatus oldStatus, AppointmentStatus newStatus) {
        this.appointment = appointment;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.patientId = appointment.getPatient() != null ? appointment.getPatient().getId() : null;
        this.timestamp = LocalDateTime.now();
    }
}