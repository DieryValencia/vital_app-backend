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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vitalapp.vital_app_backend.dto.triage.TriageCreateDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageResponseDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageUpdateDTO;
import com.vitalapp.vital_app_backend.model.TriageStatus;
import com.vitalapp.vital_app_backend.service.TriageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/triages")
public class TriageController {

    @Autowired
    private TriageService triageService;

    /**
     * GET /api/triages - Obtener todos los triages
     */
    @GetMapping
    public ResponseEntity<List<TriageResponseDTO>> getAllTriages() {
        List<TriageResponseDTO> triages = triageService.getAllTriages();
        return ResponseEntity.ok(triages);
    }

    /**
     * GET /api/triages/{id} - Obtener triage por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TriageResponseDTO> getTriageById(@PathVariable Long id) {
        TriageResponseDTO triage = triageService.getTriageById(id);
        return ResponseEntity.ok(triage);
    }

    /**
     * GET /api/triages/patient/{patientId} - Obtener triages por paciente
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TriageResponseDTO>> getTriagesByPatient(@PathVariable Long patientId) {
        List<TriageResponseDTO> triages = triageService.getTriagesByPatient(patientId);
        return ResponseEntity.ok(triages);
    }

    /**
     * GET /api/triages/status/{status} - Obtener triages por estado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TriageResponseDTO>> getTriagesByStatus(@PathVariable TriageStatus status) {
        List<TriageResponseDTO> triages = triageService.getTriagesByStatus(status);
        return ResponseEntity.ok(triages);
    }

    /**
     * POST /api/triages - Crear nuevo triage
     */
    @PostMapping
    public ResponseEntity<TriageResponseDTO> createTriage(@Valid @RequestBody TriageCreateDTO dto) {
        TriageResponseDTO createdTriage = triageService.createTriage(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTriage);
    }

    /**
     * PUT /api/triages/{id} - Actualizar triage
     */
    @PutMapping("/{id}")
    public ResponseEntity<TriageResponseDTO> updateTriage(@PathVariable Long id, @Valid @RequestBody TriageUpdateDTO dto) {
        TriageResponseDTO updatedTriage = triageService.updateTriage(id, dto);
        return ResponseEntity.ok(updatedTriage);
    }

    /**
     * PUT /api/triages/{id}/status - Actualizar estado del triage
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<TriageResponseDTO> updateTriageStatus(@PathVariable Long id, @RequestParam TriageStatus status) {
        TriageResponseDTO updatedTriage = triageService.updateTriageStatus(id, status);
        return ResponseEntity.ok(updatedTriage);
    }

    /**
     * DELETE /api/triages/{id} - Eliminar triage
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTriage(@PathVariable Long id) {
        triageService.deleteTriage(id);
        return ResponseEntity.noContent().build();
    }
}