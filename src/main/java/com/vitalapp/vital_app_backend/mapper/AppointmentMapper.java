package com.vitalapp.vital_app_backend.mapper;

import org.springframework.stereotype.Component;

import com.vitalapp.vital_app_backend.dto.appointment.AppointmentCreateDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentResponseDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentUpdateDTO;
import com.vitalapp.vital_app_backend.model.Appointment;

@Component
public class AppointmentMapper {

    /**
     * Convierte un AppointmentCreateDTO a una entidad Appointment
     */
    public Appointment toEntity(AppointmentCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Appointment.builder()
                .doctor(dto.getDoctor())
                .specialty(dto.getSpecialty())
                .scheduledAt(dto.getScheduledAt())
                .duration(dto.getDuration())
                .reason(dto.getReason())
                .build();
    }

    /**
     * Convierte una entidad Appointment a AppointmentResponseDTO
     */
    public AppointmentResponseDTO toResponseDTO(Appointment entity) {
        if (entity == null) {
            return null;
        }

        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(entity.getId());
        dto.setPatientId(entity.getPatient() != null ? entity.getPatient().getId() : null);
        dto.setPatientName(entity.getPatient() != null ? entity.getPatient().getFullName() : null);
        dto.setDoctor(entity.getDoctor());
        dto.setSpecialty(entity.getSpecialty());
        dto.setScheduledAt(entity.getScheduledAt());
        dto.setDuration(entity.getDuration());
        dto.setStatus(entity.getStatus());
        dto.setReason(entity.getReason());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setReminderSent(entity.isReminderSent());

        return dto;
    }

    /**
     * Actualiza una entidad Appointment con los datos de AppointmentUpdateDTO
     */
    public void updateEntityFromDTO(AppointmentUpdateDTO dto, Appointment entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getDoctor() != null) {
            entity.setDoctor(dto.getDoctor());
        }
        if (dto.getSpecialty() != null) {
            entity.setSpecialty(dto.getSpecialty());
        }
        if (dto.getScheduledAt() != null) {
            entity.setScheduledAt(dto.getScheduledAt());
        }
        if (dto.getDuration() != null) {
            entity.setDuration(dto.getDuration());
        }
        if (dto.getReason() != null) {
            entity.setReason(dto.getReason());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
    }
}