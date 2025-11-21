package com.vitalapp.vital_app_backend.controller;

import com.vitalapp.vital_app_backend.dto.notification.*;
import com.vitalapp.vital_app_backend.service.NotificationService;
import com.vitalapp.vital_app_backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Gestión de notificaciones del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Operation(
        summary = "Obtener todas las notificaciones",
        description = "Retorna la lista completa de notificaciones del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de notificaciones obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token requerido"
        )
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @Operation(
        summary = "Obtener notificación por ID",
        description = "Retorna la información detallada de una notificación específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notificación encontrada",
            content = @Content(schema = @Schema(implementation = NotificationResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notificación no encontrada"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(
        @Parameter(description = "ID de la notificación", example = "1")
        @PathVariable Long id
    ) {
        NotificationResponseDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @Operation(
        summary = "Obtener notificaciones por destinatario",
        description = "Retorna todas las notificaciones enviadas a un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notificaciones del destinatario obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByRecipient(
        @Parameter(description = "ID del destinatario", example = "1")
        @PathVariable Long recipientId
    ) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByRecipient(recipientId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
        summary = "Obtener notificaciones no leídas",
        description = "Retorna las notificaciones no leídas de un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notificaciones no leídas obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/recipient/{recipientId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(
        @Parameter(description = "ID del destinatario", example = "1")
        @PathVariable Long recipientId
    ) {
        List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(recipientId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
        summary = "Obtener conteo de notificaciones no leídas",
        description = "Retorna el número de notificaciones no leídas de un usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conteo obtenido exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/recipient/{recipientId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(
        @Parameter(description = "ID del destinatario", example = "1")
        @PathVariable Long recipientId
    ) {
        long count = notificationService.getUnreadCount(recipientId);
        return ResponseEntity.ok(count);
    }

    @Operation(
        summary = "Obtener notificaciones no leídas del usuario actual",
        description = "Retorna las notificaciones no leídas del usuario autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notificaciones no leídas obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getMyUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Obtener el usuario por username
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener notificaciones no leídas del usuario
        List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    @Operation(
        summary = "Crear nueva notificación",
        description = "Registra una nueva notificación en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Notificación creada exitosamente",
            content = @Content(schema = @Schema(implementation = NotificationResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la notificación a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = NotificationCreateDTO.class))
        )
        @Valid @RequestBody NotificationCreateDTO dto
    ) {
        NotificationResponseDTO createdNotification = notificationService.createNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @Operation(
        summary = "Actualizar notificación",
        description = "Actualiza la información de una notificación existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notificación actualizada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notificación no encontrada"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> updateNotification(
        @Parameter(description = "ID de la notificación") @PathVariable Long id,
        @Valid @RequestBody NotificationUpdateDTO dto
    ) {
        NotificationResponseDTO updatedNotification = notificationService.updateNotification(id, dto);
        return ResponseEntity.ok(updatedNotification);
    }

    @Operation(
        summary = "Marcar notificación como leída",
        description = "Cambia el estado de una notificación a leída"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notificación marcada como leída"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notificación no encontrada"
        )
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
        @Parameter(description = "ID de la notificación", example = "1")
        @PathVariable Long id
    ) {
        NotificationResponseDTO updatedNotification = notificationService.markAsRead(id);
        return ResponseEntity.ok(updatedNotification);
    }

    @Operation(
        summary = "Marcar todas las notificaciones como leídas",
        description = "Cambia el estado de todas las notificaciones de un usuario a leídas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Todas las notificaciones marcadas como leídas"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PutMapping("/recipient/{recipientId}/read-all")
    public ResponseEntity<Void> markAllAsRead(
        @Parameter(description = "ID del destinatario", example = "1")
        @PathVariable Long recipientId
    ) {
        notificationService.markAllAsRead(recipientId);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Eliminar notificación",
        description = "Elimina una notificación del sistema (eliminación permanente)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notificación eliminada"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
        @Parameter(description = "ID de la notificación", example = "1")
        @PathVariable Long id
    ) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Eliminar notificaciones expiradas",
        description = "Elimina todas las notificaciones que han expirado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notificaciones expiradas eliminadas"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @DeleteMapping("/expired")
    public ResponseEntity<Void> deleteExpiredNotifications() {
        notificationService.deleteExpiredNotifications();
        return ResponseEntity.ok().build();
    }
}