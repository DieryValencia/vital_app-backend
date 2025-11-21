package com.vitalapp.vital_app_backend.mapper;

import org.springframework.stereotype.Component;

import com.vitalapp.vital_app_backend.dto.triage.TriageCreateDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageResponseDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageUpdateDTO;
import com.vitalapp.vital_app_backend.model.Triage;

@Component
public class TriageMapper {

    /**
     * Convierte un TriageCreateDTO a una entidad Triage
     */
    public Triage toEntity(TriageCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Triage.builder()
                .symptoms(dto.getSymptoms())
                .bloodPressure(dto.getBloodPressure())
                .heartRate(dto.getHeartRate())
                .temperature(dto.getTemperature())
                .oxygenSaturation(dto.getOxygenSaturation())
                .severityLevel(dto.getSeverityLevel())
                .recommendedAction(dto.getRecommendedAction())
                .build();
    }

    /**
     * Convierte una entidad Triage a TriageResponseDTO
     */
    public TriageResponseDTO toResponseDTO(Triage entity) {
        if (entity == null) {
            return null;
        }

        TriageResponseDTO dto = new TriageResponseDTO();
        dto.setId(entity.getId());
        dto.setPatientId(entity.getPatient() != null ? entity.getPatient().getId() : null);
        dto.setPatientName(entity.getPatient() != null ? entity.getPatient().getFullName() : null);
        dto.setSymptoms(entity.getSymptoms());
        dto.setBloodPressure(entity.getBloodPressure());
        dto.setHeartRate(entity.getHeartRate());
        dto.setTemperature(entity.getTemperature());
        dto.setOxygenSaturation(entity.getOxygenSaturation());
        dto.setSeverityLevel(entity.getSeverityLevel());
        dto.setRecommendedAction(entity.getRecommendedAction());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedById(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null);
        dto.setCreatedByName(entity.getCreatedBy() != null ? entity.getCreatedBy().getUsername() : null);
        dto.setNotes(entity.getNotes());

        return dto;
    }

    /**
     * Actualiza una entidad Triage con los datos de TriageUpdateDTO
     */
    public void updateEntityFromDTO(TriageUpdateDTO dto, Triage entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getBloodPressure() != null) {
            entity.setBloodPressure(dto.getBloodPressure());
        }
        if (dto.getHeartRate() != null) {
            entity.setHeartRate(dto.getHeartRate());
        }
        if (dto.getTemperature() != null) {
            entity.setTemperature(dto.getTemperature());
        }
        if (dto.getOxygenSaturation() != null) {
            entity.setOxygenSaturation(dto.getOxygenSaturation());
        }
        if (dto.getRecommendedAction() != null) {
            entity.setRecommendedAction(dto.getRecommendedAction());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
    }
}