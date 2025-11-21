package com.vitalapp.vital_app_backend.controller;

import com.vitalapp.vital_app_backend.dto.appointment.*;
import com.vitalapp.vital_app_backend.model.AppointmentStatus;
import com.vitalapp.vital_app_backend.service.AppointmentService;
import com.vitalapp.vital_app_backend.service.AuthorizationService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Gestión de citas médicas")
@SecurityRequirement(name = "Bearer Authentication")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AuthorizationService authorizationService;

    /**
     * Helper method to build OK response
     */
    private <T> ResponseEntity<T> buildOkResponse(T data) {
        return ResponseEntity.ok(data);
    }

    @Operation(
        summary = "Obtener todas las citas",
        description = "Retorna la lista completa de citas médicas registradas en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de citas obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token requerido"
        )
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return buildOkResponse(appointmentService.getAllAppointments());
    }

    @Operation(
        summary = "Obtener cita por ID",
        description = "Retorna la información detallada de una cita específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cita encontrada",
            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cita no encontrada"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(
        @Parameter(description = "ID de la cita", example = "1")
        @PathVariable Long id
    ) {
        return buildOkResponse(appointmentService.getAppointmentById(id));
    }

    @Operation(
        summary = "Obtener citas por paciente",
        description = "Retorna todas las citas asociadas a un paciente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Citas del paciente obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPatient(
        @Parameter(description = "ID del paciente", example = "1")
        @PathVariable Long patientId
    ) {
        return buildOkResponse(appointmentService.getAppointmentsByPatient(patientId));
    }

    @Operation(
        summary = "Obtener citas por estado",
        description = "Retorna todas las citas que tienen un estado específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Citas por estado obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByStatus(
        @Parameter(description = "Estado de la cita", example = "SCHEDULED")
        @PathVariable AppointmentStatus status
    ) {
        return buildOkResponse(appointmentService.getAppointmentsByStatus(status));
    }

    @Operation(
        summary = "Obtener citas próximas",
        description = "Retorna las citas programadas para fechas futuras"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Citas próximas obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponseDTO>> getUpcomingAppointments() {
        return buildOkResponse(appointmentService.getUpcomingAppointments());
    }

    @Operation(
        summary = "Crear nueva cita",
        description = "Registra una nueva cita médica en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cita creada exitosamente",
            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))
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
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la cita a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = AppointmentCreateDTO.class))
        )
        @Valid @RequestBody AppointmentCreateDTO dto
    ) {
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @Operation(
        summary = "Actualizar cita",
        description = "Actualiza la información de una cita existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cita actualizada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cita no encontrada"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
        @Parameter(description = "ID de la cita") @PathVariable Long id,
        @Valid @RequestBody AppointmentUpdateDTO dto
    ) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, dto);
        return ResponseEntity.ok(updatedAppointment);
    }

    @Operation(
        summary = "Actualizar estado de la cita",
        description = "Cambia el estado de una cita médica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estado de la cita actualizado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cita no encontrada"
        )
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateAppointmentStatus(
        @Parameter(description = "ID de la cita") @PathVariable Long id,
        @Parameter(description = "Nuevo estado de la cita", example = "COMPLETED")
        @RequestParam AppointmentStatus status
    ) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(updatedAppointment);
    }

    @Operation(
        summary = "Cancelar cita",
        description = "Cancela una cita médica con una razón opcional"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cita cancelada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cita no encontrada"
        )
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
        @Parameter(description = "ID de la cita") @PathVariable Long id,
        @Parameter(description = "Razón de la cancelación")
        @RequestParam(required = false) String reason
    ) {
        AppointmentResponseDTO cancelledAppointment = appointmentService.cancelAppointment(id, reason);
        return ResponseEntity.ok(cancelledAppointment);
    }

    @Operation(
        summary = "Eliminar cita",
        description = "Elimina una cita del sistema (eliminación permanente)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cita eliminada"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
        @Parameter(description = "ID de la cita", example = "1")
        @PathVariable Long id
    ) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}