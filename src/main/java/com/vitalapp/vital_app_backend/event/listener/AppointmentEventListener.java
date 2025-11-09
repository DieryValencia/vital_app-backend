package com.vitalapp.vital_app_backend.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vitalapp.vital_app_backend.event.AppointmentCreatedEvent;
import com.vitalapp.vital_app_backend.event.AppointmentStatusChangedEvent;
import com.vitalapp.vital_app_backend.model.AppointmentStatus;
import com.vitalapp.vital_app_backend.model.Notification;
import com.vitalapp.vital_app_backend.model.NotificationPriority;
import com.vitalapp.vital_app_backend.model.NotificationType;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.repository.PatientRepository;
import com.vitalapp.vital_app_backend.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final NotificationService notificationService;
    private final PatientRepository patientRepository;

    @EventListener
    @Async
    public void handleAppointmentCreated(AppointmentCreatedEvent event) {
        try {
            log.info("Procesando evento de creación de cita para paciente ID: {}", event.getPatientId());

            // Obtener el paciente para verificar si tiene usuario asociado
            Patient patient = patientRepository.findById(event.getPatientId()).orElse(null);
            if (patient == null) {
                log.warn("Paciente no encontrado con ID: {}", event.getPatientId());
                return;
            }

            // Verificar si el paciente tiene un usuario asociado (por ahora asumimos que no, según el modelo actual)
            // En el modelo actual, Patient no tiene relación directa con User
            // Podríamos necesitar modificar el modelo o buscar por email/nombre si es necesario
            // Por ahora, no enviamos notificación ya que no hay usuario asociado al paciente

            log.info("Cita creada, pero no se envía notificación ya que el paciente no tiene usuario asociado");

        } catch (Exception e) {
            log.error("Error procesando evento AppointmentCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @EventListener
    @Async
    public void handleAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        try {
            log.info("Procesando cambio de status de cita: {} -> {}",
                    event.getOldStatus(), event.getNewStatus());

            // Obtener el paciente
            Patient patient = patientRepository.findById(event.getPatientId()).orElse(null);
            if (patient == null) {
                log.warn("Paciente no encontrado con ID: {}", event.getPatientId());
                return;
            }

            // Verificar el nuevo status y crear notificación apropiada
            Notification notification = null;

            if (event.getNewStatus() == AppointmentStatus.CONFIRMED) {
                notification = Notification.builder()
                        .recipient(null) // No hay usuario asociado al paciente
                        .title("✅ Cita Confirmada")
                        .message("Tu cita del " + event.getAppointment().getScheduledAt() + " ha sido confirmada")
                        .type(NotificationType.SUCCESS)
                        .priority(NotificationPriority.MEDIUM)
                        .relatedEntityType("APPOINTMENT")
                        .relatedEntityId(event.getAppointment().getId())
                        .read(false)
                        .build();
            } else if (event.getNewStatus() == AppointmentStatus.CANCELLED) {
                notification = Notification.builder()
                        .recipient(null) // No hay usuario asociado al paciente
                        .title("❌ Cita Cancelada")
                        .message("Tu cita del " + event.getAppointment().getScheduledAt() + " ha sido cancelada")
                        .type(NotificationType.WARNING)
                        .priority(NotificationPriority.HIGH)
                        .relatedEntityType("APPOINTMENT")
                        .relatedEntityId(event.getAppointment().getId())
                        .read(false)
                        .build();
            } else if (event.getNewStatus() == AppointmentStatus.IN_PROGRESS) {
                notification = Notification.builder()
                        .recipient(null) // No hay usuario asociado al paciente
                        .title("🏥 Cita en Progreso")
                        .message("Tu cita está en progreso")
                        .type(NotificationType.INFO)
                        .priority(NotificationPriority.LOW)
                        .relatedEntityType("APPOINTMENT")
                        .relatedEntityId(event.getAppointment().getId())
                        .read(false)
                        .build();
            }

            // Solo guardar si se creó una notificación
            if (notification != null) {
                // Como no hay usuario asociado, podríamos guardar en una tabla temporal
                // o modificar el modelo para permitir notificaciones sin recipient
                // Por ahora, solo loggeamos
                log.info("Notificación preparada pero no enviada (paciente sin usuario): {}",
                        notification.getTitle());
            }

        } catch (Exception e) {
            log.error("Error procesando evento AppointmentStatusChangedEvent: {}", e.getMessage(), e);
        }
    }
}