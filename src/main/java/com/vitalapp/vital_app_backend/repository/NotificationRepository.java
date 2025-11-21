package com.vitalapp.vital_app_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vitalapp.vital_app_backend.model.Notification;
import com.vitalapp.vital_app_backend.model.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Busca notificaciones por destinatario
     */
    List<Notification> findByRecipientId(Long recipientId);

    /**
     * Busca notificaciones no leídas por destinatario
     */
    List<Notification> findByRecipientIdAndReadFalse(Long recipientId);

    /**
     * Busca notificaciones por destinatario ordenadas por fecha de creación descendente
     */
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    /**
     * Busca notificaciones por destinatario y tipo
     */
    List<Notification> findByRecipientIdAndType(Long recipientId, NotificationType type);

    /**
     * Cuenta notificaciones no leídas por destinatario
     */
    long countByRecipientIdAndReadFalse(Long recipientId);
}