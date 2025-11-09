package com.vitalapp.vital_app_backend.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vitalapp.vital_app_backend.event.TriageCreatedEvent;
import com.vitalapp.vital_app_backend.model.Notification;
import com.vitalapp.vital_app_backend.model.NotificationPriority;
import com.vitalapp.vital_app_backend.model.NotificationType;
import com.vitalapp.vital_app_backend.model.Role;
import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.repository.UserRepository;
import com.vitalapp.vital_app_backend.service.NotificationService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TriageEventListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @EventListener
    @Async
    public void handleTriageCreated(TriageCreatedEvent event) {
        try {
            log.info("Procesando evento de creación de triage con severidad: {}", event.getSeverityLevel());

            if (event.getSeverityLevel() >= 4) {
                // Buscar usuarios con rol ADMIN o DOCTOR
                List<User> adminUsers = userRepository.findByRoleIn(List.of(Role.ADMIN, Role.DOCTOR));

                if (adminUsers.isEmpty()) {
                    log.warn("No se encontraron usuarios ADMIN o DOCTOR para notificar");
                    return;
                }

                for (User admin : adminUsers) {
                    try {
                        Notification notification = Notification.builder()
                                .recipient(admin)
                                .title("⚠️ TRIAJE DE ALTA PRIORIDAD")
                                .message("Nuevo triaje con severidad " + event.getSeverityLevel() + " para paciente ID: " + event.getPatientId())
                                .type(NotificationType.ALERT)
                                .priority(event.getSeverityLevel() == 5 ? NotificationPriority.URGENT : NotificationPriority.HIGH)
                                .relatedEntityType("TRIAGE")
                                .relatedEntityId(event.getTriage().getId())
                                .read(false)
                                .build();

                        notificationService.saveNotification(notification);
                        log.info("Notificación enviada a usuario {} por triage de alta prioridad", admin.getUsername());
                    } catch (Exception e) {
                        log.error("Error al crear notificación para usuario {}: {}", admin.getUsername(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error procesando evento TriageCreatedEvent: {}", e.getMessage(), e);
        }
    }
}