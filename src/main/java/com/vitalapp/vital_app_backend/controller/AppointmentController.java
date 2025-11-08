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

import com.vitalapp.vital_app_backend.dto.appointment.AppointmentCreateDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentResponseDTO;
import com.vitalapp.vital_app_backend.dto.appointment.AppointmentUpdateDTO;
import com.vitalapp.vital_app_backend.model.AppointmentStatus;
import com.vitalapp.vital_app_backend.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * GET /api/appointments - Obtener todas las citas
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * GET /api/appointments/{id} - Obtener cita por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * GET /api/appointments/patient/{patientId} - Obtener citas por paciente
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPatient(@PathVariable Long patientId) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * GET /api/appointments/status/{status} - Obtener citas por estado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }

    /**
     * GET /api/appointments/upcoming - Obtener citas próximas
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponseDTO>> getUpcomingAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * POST /api/appointments - Crear nueva cita
     */
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentCreateDTO dto) {
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    /**
     * PUT /api/appointments/{id} - Actualizar cita
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateDTO dto) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, dto);
        return ResponseEntity.ok(updatedAppointment);
    }

    /**
     * PUT /api/appointments/{id}/status - Actualizar estado de la cita
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateAppointmentStatus(@PathVariable Long id, @RequestParam AppointmentStatus status) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(updatedAppointment);
    }

    /**
     * PUT /api/appointments/{id}/cancel - Cancelar cita
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable Long id, @RequestParam(required = false) String reason) {
        AppointmentResponseDTO cancelledAppointment = appointmentService.cancelAppointment(id, reason);
        return ResponseEntity.ok(cancelledAppointment);
    }

    /**
     * DELETE /api/appointments/{id} - Eliminar cita
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}