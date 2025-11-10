package com.vitalapp.vital_app_backend.controller;

import com.vitalapp.vital_app_backend.dto.common.PageResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.*;
import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.service.PatientService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de pacientes en el sistema VitalApp.
 *
 * Este controlador proporciona endpoints para realizar operaciones CRUD completas
 * sobre pacientes, incluyendo búsqueda avanzada, filtrado, paginación y ordenamiento.
 * Todas las operaciones requieren autenticación JWT.
 *
 * @author Equipo VitalApp
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Gestión de pacientes del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class PatientController {

    /**
     * Servicio de pacientes inyectado por Spring.
     * Maneja toda la lógica de negocio relacionada con pacientes.
     */
    private final PatientService patientService;

    @Operation(
        summary = "Obtener todos los pacientes",
        description = "Retorna la lista completa de pacientes registrados en el sistema con paginación, filtros y ordenamiento"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de pacientes obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token requerido"
        )
    })
    @GetMapping
    public ResponseEntity<PageResponseDTO<PatientResponseDTO>> getAllPatients(
            @Parameter(description = "Número de página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenar", example = "fullName")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección de ordenamiento", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @Parameter(description = "Filtrar por nombre completo")
            @RequestParam(required = false) String fullName,
            @Parameter(description = "Filtrar por número de documento")
            @RequestParam(required = false) String documentNumber,
            @Parameter(description = "Filtrar por teléfono")
            @RequestParam(required = false) String phone,
            @Parameter(description = "Filtrar por género")
            @RequestParam(required = false) Gender gender,
            @Parameter(description = "Fecha de nacimiento desde (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate birthDateFrom,
            @Parameter(description = "Fecha de nacimiento hasta (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate birthDateTo,
            @Parameter(description = "Filtrar por estado activo")
            @RequestParam(required = false) Boolean active) {

        PageResponseDTO<PatientResponseDTO> patients = patientService.getAllPatients(
            page, size, sortBy, sortDirection,
            fullName, documentNumber, phone, gender,
            birthDateFrom, birthDateTo, active
        );

        return ResponseEntity.ok(patients);
    }

    @Operation(
        summary = "Obtener pacientes activos",
        description = "Retorna la lista de pacientes que están marcados como activos en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de pacientes activos obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/active")
    public ResponseEntity<List<PatientResponseDTO>> getActivePatients() {
        List<PatientResponseDTO> patients = patientService.getActivePatients();
        return ResponseEntity.ok(patients);
    }

    @Operation(
        summary = "Obtener paciente por ID",
        description = "Retorna la información detallada de un paciente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Paciente encontrado",
            content = @Content(schema = @Schema(implementation = PatientResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Paciente no encontrado"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(
        @Parameter(description = "ID del paciente", example = "1")
        @PathVariable Long id
    ) {
        PatientResponseDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @Operation(
        summary = "Obtener paciente por documento",
        description = "Busca un paciente específico usando su número de documento"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Paciente encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Paciente no encontrado"
        )
    })
    @GetMapping("/document/{documentNumber}")
    public ResponseEntity<PatientResponseDTO> getPatientByDocument(
        @Parameter(description = "Número de documento del paciente", example = "12345678")
        @PathVariable String documentNumber
    ) {
        PatientResponseDTO patient = patientService.getPatientByDocument(documentNumber);
        return ResponseEntity.ok(patient);
    }

    @Operation(
        summary = "Buscar pacientes por nombre",
        description = "Busca pacientes que coincidan con el nombre proporcionado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Búsqueda realizada exitosamente"
        )
    })
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(
        @Parameter(description = "Nombre a buscar", example = "Juan")
        @RequestParam String name
    ) {
        List<PatientResponseDTO> patients = patientService.searchPatientsByName(name);
        return ResponseEntity.ok(patients);
    }

    @Operation(
        summary = "Crear nuevo paciente",
        description = "Registra un nuevo paciente en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Paciente creado exitosamente",
            content = @Content(schema = @Schema(implementation = PatientResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o documento duplicado"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del paciente a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = PatientCreateDTO.class))
        )
        @Valid @RequestBody PatientCreateDTO dto
    ) {
        PatientResponseDTO createdPatient = patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @Operation(
        summary = "Actualizar paciente",
        description = "Actualiza la información de un paciente existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Paciente actualizado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Paciente no encontrado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(
        @Parameter(description = "ID del paciente") @PathVariable Long id,
        @Valid @RequestBody PatientUpdateDTO dto
    ) {
        PatientResponseDTO updatedPatient = patientService.updatePatient(id, dto);
        return ResponseEntity.ok(updatedPatient);
    }

    @Operation(
        summary = "Desactivar paciente",
        description = "Desactiva un paciente sin eliminarlo permanentemente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Paciente desactivado"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePatient(@PathVariable Long id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Eliminar paciente",
        description = "Elimina un paciente del sistema (eliminación permanente)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Paciente eliminado"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}