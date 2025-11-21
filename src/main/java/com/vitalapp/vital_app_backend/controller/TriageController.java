package com.vitalapp.vital_app_backend.controller;

import com.vitalapp.vital_app_backend.dto.triage.*;
import com.vitalapp.vital_app_backend.model.TriageStatus;
import com.vitalapp.vital_app_backend.service.AuthorizationService;
import com.vitalapp.vital_app_backend.service.TriageService;
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
@RequestMapping("/api/triages")
@RequiredArgsConstructor
@Tag(name = "Triages", description = "Gestión de triajes médicos")
@SecurityRequirement(name = "Bearer Authentication")
public class TriageController {

    private final TriageService triageService;
    private final AuthorizationService authorizationService;

    @Operation(
        summary = "Obtener todos los triajes",
        description = "Retorna la lista completa de triajes médicos registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de triajes obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token requerido"
        )
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<TriageResponseDTO>> getAllTriages() {
        List<TriageResponseDTO> triages = triageService.getAllTriages();
        return ResponseEntity.ok(triages);
    }

    @Operation(
        summary = "Obtener triaje por ID",
        description = "Retorna la información detallada de un triaje específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Triaje encontrado",
            content = @Content(schema = @Schema(implementation = TriageResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Triaje no encontrado"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<TriageResponseDTO> getTriageById(
        @Parameter(description = "ID del triaje", example = "1")
        @PathVariable Long id
    ) {
        TriageResponseDTO triage = triageService.getTriageById(id);
        return ResponseEntity.ok(triage);
    }

    @Operation(
        summary = "Obtener triajes por paciente",
        description = "Retorna todos los triajes asociados a un paciente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Triajes del paciente obtenidos exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TriageResponseDTO>> getTriagesByPatient(
        @Parameter(description = "ID del paciente", example = "1")
        @PathVariable Long patientId
    ) {
        List<TriageResponseDTO> triages = triageService.getTriagesByPatient(patientId);
        return ResponseEntity.ok(triages);
    }

    @Operation(
        summary = "Obtener triajes por estado",
        description = "Retorna todos los triajes que tienen un estado específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Triajes por estado obtenidos exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TriageResponseDTO>> getTriagesByStatus(
        @Parameter(description = "Estado del triaje", example = "PENDING")
        @PathVariable TriageStatus status
    ) {
        List<TriageResponseDTO> triages = triageService.getTriagesByStatus(status);
        return ResponseEntity.ok(triages);
    }

    @Operation(
        summary = "Crear nuevo triaje",
        description = "Registra un nuevo triaje médico en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Triaje creado exitosamente",
            content = @Content(schema = @Schema(implementation = TriageResponseDTO.class))
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
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<TriageResponseDTO> createTriage(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del triaje a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = TriageCreateDTO.class))
        )
        @Valid @RequestBody TriageCreateDTO dto
    ) {
        TriageResponseDTO createdTriage = triageService.createTriage(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTriage);
    }

    @Operation(
        summary = "Actualizar triaje",
        description = "Actualiza la información de un triaje existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Triaje actualizado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Triaje no encontrado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        )
    })
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<TriageResponseDTO> updateTriage(
        @Parameter(description = "ID del triaje") @PathVariable Long id,
        @Valid @RequestBody TriageUpdateDTO dto
    ) {
        TriageResponseDTO updatedTriage = triageService.updateTriage(id, dto);
        return ResponseEntity.ok(updatedTriage);
    }

    @Operation(
        summary = "Actualizar estado del triaje",
        description = "Cambia el estado de un triaje médico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estado del triaje actualizado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Triaje no encontrado"
        )
    })
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/status")
    public ResponseEntity<TriageResponseDTO> updateTriageStatus(
        @Parameter(description = "ID del triaje") @PathVariable Long id,
        @Parameter(description = "Nuevo estado del triaje", example = "IN_PROGRESS")
        @RequestParam TriageStatus status
    ) {
        TriageResponseDTO updatedTriage = triageService.updateTriageStatus(id, status);
        return ResponseEntity.ok(updatedTriage);
    }

    @Operation(
        summary = "Eliminar triaje",
        description = "Elimina un triaje del sistema (eliminación permanente)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Triaje eliminado"),
        @ApiResponse(responseCode = "404", description = "Triaje no encontrado")
    })
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTriage(
        @Parameter(description = "ID del triaje", example = "1")
        @PathVariable Long id
    ) {
        triageService.deleteTriage(id);
        return ResponseEntity.noContent().build();
    }
}