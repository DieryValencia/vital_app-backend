package com.vitalapp.vital_app_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalapp.vital_app_backend.dto.notification.NotificationCreateDTO;
import com.vitalapp.vital_app_backend.dto.notification.NotificationResponseDTO;
import com.vitalapp.vital_app_backend.dto.notification.NotificationUpdateDTO;
import com.vitalapp.vital_app_backend.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/notifications - Obtener todas las notificaciones
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/{id} - Obtener notificación por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        NotificationResponseDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    /**
     * GET /api/notifications/recipient/{recipientId} - Obtener notificaciones por destinatario
     */
    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByRecipient(@PathVariable Long recipientId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByRecipient(recipientId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/recipient/{recipientId}/unread - Obtener notificaciones no leídas
     */
    @GetMapping("/recipient/{recipientId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long recipientId) {
        List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(recipientId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/recipient/{recipientId}/unread/count - Obtener conteo de no leídas
     */
    @GetMapping("/recipient/{recipientId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long recipientId) {
        long count = notificationService.getUnreadCount(recipientId);
        return ResponseEntity.ok(count);
    }

    /**
     * POST /api/notifications - Crear nueva notificación
     */
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(@Valid @RequestBody NotificationCreateDTO dto) {
        NotificationResponseDTO createdNotification = notificationService.createNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    /**
     * PUT /api/notifications/{id} - Actualizar notificación
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> updateNotification(@PathVariable Long id, @Valid @RequestBody NotificationUpdateDTO dto) {
        NotificationResponseDTO updatedNotification = notificationService.updateNotification(id, dto);
        return ResponseEntity.ok(updatedNotification);
    }

    /**
     * PUT /api/notifications/{id}/read - Marcar como leída
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long id) {
        NotificationResponseDTO updatedNotification = notificationService.markAsRead(id);
        return ResponseEntity.ok(updatedNotification);
    }

    /**
     * PUT /api/notifications/recipient/{recipientId}/read-all - Marcar todas como leídas
     */
    @PutMapping("/recipient/{recipientId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long recipientId) {
        notificationService.markAllAsRead(recipientId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/notifications/{id} - Eliminar notificación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/notifications/expired - Eliminar notificaciones expiradas
     */
    @DeleteMapping("/expired")
    public ResponseEntity<Void> deleteExpiredNotifications() {
        notificationService.deleteExpiredNotifications();
        return ResponseEntity.ok().build();
    }
}