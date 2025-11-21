package com.vitalapp.vital_app_backend.dto.notification;

import jakarta.validation.constraints.Size;

public class NotificationUpdateDTO {

    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String title;

    @Size(max = 1000, message = "El mensaje no puede exceder 1000 caracteres")
    private String message;

    // Getters and setters
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
}