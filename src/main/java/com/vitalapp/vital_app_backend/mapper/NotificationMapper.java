package com.vitalapp.vital_app_backend.mapper;

import org.springframework.stereotype.Component;

import com.vitalapp.vital_app_backend.dto.notification.NotificationCreateDTO;
import com.vitalapp.vital_app_backend.dto.notification.NotificationResponseDTO;
import com.vitalapp.vital_app_backend.dto.notification.NotificationUpdateDTO;
import com.vitalapp.vital_app_backend.model.Notification;

@Component
public class NotificationMapper {

    /**
     * Convierte un NotificationCreateDTO a una entidad Notification
     */
    public Notification toEntity(NotificationCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Notification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .priority(dto.getPriority())
                .build();
    }

    /**
     * Convierte una entidad Notification a NotificationResponseDTO
     */
    public NotificationResponseDTO toResponseDTO(Notification entity) {
        if (entity == null) {
            return null;
        }

        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setRecipientId(entity.getRecipient() != null ? entity.getRecipient().getId() : null);
        dto.setRecipientName(entity.getRecipient() != null ? entity.getRecipient().getUsername() : null);
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setPriority(entity.getPriority());
        dto.setRead(entity.isRead());
        dto.setReadAt(entity.getReadAt());
        dto.setRelatedEntityType(entity.getRelatedEntityType());
        dto.setRelatedEntityId(entity.getRelatedEntityId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExpiresAt(entity.getExpiresAt());

        return dto;
    }

    /**
     * Actualiza una entidad Notification con los datos de NotificationUpdateDTO
     */
    public void updateEntityFromDTO(NotificationUpdateDTO dto, Notification entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getMessage() != null) {
            entity.setMessage(dto.getMessage());
        }
    }
}