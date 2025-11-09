package com.vitalapp.vital_app_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitalapp.vital_app_backend.dto.patient.PatientCreateDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientUpdateDTO;
import com.vitalapp.vital_app_backend.exception.custom.ResourceNotFoundException;
import com.vitalapp.vital_app_backend.exception.custom.DuplicateResourceException;
import com.vitalapp.vital_app_backend.mapper.PatientMapper;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.repository.PatientRepository;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    /**
     * Crea un nuevo paciente
     */
    public PatientResponseDTO createPatient(PatientCreateDTO dto) {
        if (patientRepository.existsByDocumentNumber(dto.getDocumentNumber())) {
            throw new DuplicateResourceException("Ya existe un paciente con el documento: " + dto.getDocumentNumber());
        }

        Patient patient = patientMapper.toEntity(dto);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponseDTO(savedPatient);
    }

    /**
     * Obtiene todos los pacientes
     */
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene pacientes activos
     */
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getActivePatients() {
        return patientRepository.findByActiveTrue().stream()
                .map(patientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un paciente por ID
     */
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        return patientMapper.toResponseDTO(patient);
    }

    /**
     * Obtiene un paciente por número de documento
     */
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByDocument(String documentNumber) {
        Patient patient = patientRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con documento: " + documentNumber));
        return patientMapper.toResponseDTO(patient);
    }

    /**
     * Busca pacientes por nombre
     */
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> searchPatientsByName(String name) {
        return patientRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(patientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un paciente
     */
    public PatientResponseDTO updatePatient(Long id, PatientUpdateDTO dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        patientMapper.updateEntityFromDTO(dto, patient);
        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toResponseDTO(updatedPatient);
    }

    /**
     * Desactiva un paciente
     */
    public void deactivatePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        patient.setActive(false);
        patientRepository.save(patient);
    }

    /**
     * Elimina un paciente
     */
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + id);
        }
        patientRepository.deleteById(id);
    }
}