package com.vitalapp.vital_app_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vitalapp.vital_app_backend.dto.patient.PatientCreateDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientUpdateDTO;
import com.vitalapp.vital_app_backend.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * GET /api/patients - Obtener todos los pacientes
     */
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * GET /api/patients/active - Obtener pacientes activos
     */
    @GetMapping("/active")
    public ResponseEntity<List<PatientResponseDTO>> getActivePatients() {
        List<PatientResponseDTO> patients = patientService.getActivePatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * GET /api/patients/{id} - Obtener paciente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        PatientResponseDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    /**
     * GET /api/patients/document/{documentNumber} - Obtener paciente por documento
     */
    @GetMapping("/document/{documentNumber}")
    public ResponseEntity<PatientResponseDTO> getPatientByDocument(@PathVariable String documentNumber) {
        PatientResponseDTO patient = patientService.getPatientByDocument(documentNumber);
        return ResponseEntity.ok(patient);
    }

    /**
     * GET /api/patients/search - Buscar pacientes por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(@RequestParam String name) {
        List<PatientResponseDTO> patients = patientService.searchPatientsByName(name);
        return ResponseEntity.ok(patients);
    }

    /**
     * POST /api/patients - Crear nuevo paciente
     */
    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientCreateDTO dto) {
        PatientResponseDTO createdPatient = patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    /**
     * PUT /api/patients/{id} - Actualizar paciente
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientUpdateDTO dto) {
        PatientResponseDTO updatedPatient = patientService.updatePatient(id, dto);
        return ResponseEntity.ok(updatedPatient);
    }

    /**
     * PATCH /api/patients/{id}/deactivate - Desactivar paciente
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePatient(@PathVariable Long id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/patients/{id} - Eliminar paciente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}