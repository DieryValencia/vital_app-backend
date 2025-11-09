package com.vitalapp.vital_app_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitalapp.vital_app_backend.dto.notification.NotificationCreateDTO;
import com.vitalapp.vital_app_backend.dto.notification.NotificationResponseDTO;
import com.vitalapp.vital_app_backend.dto.notification.NotificationUpdateDTO;
import com.vitalapp.vital_app_backend.mapper.NotificationMapper;
import com.vitalapp.vital_app_backend.model.Notification;
import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.repository.NotificationRepository;
import com.vitalapp.vital_app_backend.repository.UserRepository;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * Crea una nueva notificación
     */
    public NotificationResponseDTO createNotification(NotificationCreateDTO dto) {
        User recipient = userRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getRecipientId()));

        Notification notification = notificationMapper.toEntity(dto);
        notification.setRecipient(recipient);

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toResponseDTO(savedNotification);
    }

    /**
     * Obtiene todas las notificaciones
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una notificación por ID
     */
    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));
        return notificationMapper.toResponseDTO(notification);
    }

    /**
     * Actualiza una notificación
     */
    public NotificationResponseDTO updateNotification(Long id, NotificationUpdateDTO dto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));

        notificationMapper.updateEntityFromDTO(dto, notification);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toResponseDTO(updatedNotification);
    }

    /**
     * Elimina una notificación
     */
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notificación no encontrada con ID: " + id);
        }
        notificationRepository.deleteById(id);
    }

    /**
     * Obtiene notificaciones por destinatario
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId).stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene notificaciones no leídas por destinatario
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long recipientId) {
        return notificationRepository.findByRecipientIdAndReadFalse(recipientId).stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca una notificación como leída
     */
    public NotificationResponseDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toResponseDTO(updatedNotification);
    }

    /**
     * Marca todas las notificaciones como leídas para un destinatario
     */
    public void markAllAsRead(Long recipientId) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndReadFalse(recipientId);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Obtiene el conteo de notificaciones no leídas
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    /**
     * Elimina notificaciones expiradas
     */
    public void deleteExpiredNotifications() {
        List<Notification> allNotifications = notificationRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        List<Notification> expiredNotifications = allNotifications.stream()
                .filter(notification -> notification.getExpiresAt() != null && notification.getExpiresAt().isBefore(now))
                .collect(Collectors.toList());

        if (!expiredNotifications.isEmpty()) {
            notificationRepository.deleteAll(expiredNotifications);
        }
    }

    /**
     * Guarda una notificación directamente (para uso interno de listeners)
     */
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}