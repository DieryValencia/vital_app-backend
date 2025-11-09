package com.vitalapp.vital_app_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitalapp.vital_app_backend.dto.common.PageResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientCreateDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientUpdateDTO;
import com.vitalapp.vital_app_backend.exception.custom.ResourceNotFoundException;
import com.vitalapp.vital_app_backend.exception.custom.DuplicateResourceException;
import com.vitalapp.vital_app_backend.mapper.PatientMapper;
import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.repository.PatientRepository;
import com.vitalapp.vital_app_backend.specification.PatientSpecification;

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
     * Obtiene todos los pacientes con paginación, ordenamiento y filtros
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<PatientResponseDTO> getAllPatients(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String fullName,
            String documentNumber,
            String phone,
            Gender gender,
            LocalDate birthDateFrom,
            LocalDate birthDateTo,
            Boolean active) {

        // Validar y configurar ordenamiento
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        // Validar campo de ordenamiento
        String validSortBy = validateSortField(sortBy);

        // Crear Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, validSortBy));

        // Crear especificación con filtros
        Specification<Patient> spec = PatientSpecification.filterBy(
            fullName,
            documentNumber,
            phone,
            gender,
            birthDateFrom,
            birthDateTo,
            active
        );

        // Ejecutar query con paginación y filtros
        Page<Patient> patientPage = patientRepository.findAll(spec, pageable);

        // Convertir a DTO
        Page<PatientResponseDTO> dtoPage = patientPage.map(patientMapper::toResponseDTO);

        return PageResponseDTO.from(dtoPage);
    }

    /**
     * Valida que el campo de ordenamiento sea válido
     */
    private String validateSortField(String sortBy) {
        List<String> validFields = List.of(
            "id", "fullName", "documentNumber", "birthDate",
            "phone", "address", "gender", "active"
        );

        return validFields.contains(sortBy) ? sortBy : "id";
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