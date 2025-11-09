package com.vitalapp.vital_app_backend.event;

import com.vitalapp.vital_app_backend.model.Triage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TriageCreatedEvent {
    private final Triage triage;
    private final Long patientId;
    private final Integer severityLevel;
    private final LocalDateTime timestamp;

    // Constructor adicional que recibe solo Triage
    public TriageCreatedEvent(Triage triage) {
        this.triage = triage;
        this.patientId = triage.getPatient() != null ? triage.getPatient().getId() : null;
        this.severityLevel = triage.getSeverityLevel();
        this.timestamp = LocalDateTime.now();
    }
}