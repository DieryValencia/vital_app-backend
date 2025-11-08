package com.vitalapp.vital_app_backend.dto.notification;

import com.vitalapp.vital_app_backend.model.NotificationPriority;
import com.vitalapp.vital_app_backend.model.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotificationCreateDTO {

    @NotNull(message = "El ID del destinatario es obligatorio")
    private Long recipientId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String title;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede exceder 1000 caracteres")
    private String message;

    @NotNull(message = "El tipo es obligatorio")
    private NotificationType type;

    @NotNull(message = "La prioridad es obligatoria")
    private NotificationPriority priority;

    // Getters and setters
    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
}