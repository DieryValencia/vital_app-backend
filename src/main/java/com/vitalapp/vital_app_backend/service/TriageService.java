package com.vitalapp.vital_app_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitalapp.vital_app_backend.dto.triage.TriageCreateDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageResponseDTO;
import com.vitalapp.vital_app_backend.dto.triage.TriageUpdateDTO;
import com.vitalapp.vital_app_backend.event.TriageCreatedEvent;
import com.vitalapp.vital_app_backend.exception.custom.ResourceNotFoundException;
import com.vitalapp.vital_app_backend.mapper.TriageMapper;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.model.Triage;
import com.vitalapp.vital_app_backend.model.TriageStatus;
import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.repository.PatientRepository;
import com.vitalapp.vital_app_backend.repository.TriageRepository;
import com.vitalapp.vital_app_backend.repository.UserRepository;

@Service
@Transactional
public class TriageService {

    private static final Logger logger = LoggerFactory.getLogger(TriageService.class);

    @Autowired
    private TriageRepository triageRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TriageMapper triageMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Crea un nuevo triage
     */
    public TriageResponseDTO createTriage(TriageCreateDTO dto) {
        logger.info("Creando triage para paciente ID: {}", dto.getPatientId());

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + dto.getPatientId()));

        Triage triage = triageMapper.toEntity(dto);
        triage.setPatient(patient);
        triage.setStatus(TriageStatus.PENDING);

        Triage savedTriage = triageRepository.save(triage);
        logger.info("Triage creado con ID: {}", savedTriage.getId());

        // Publicar evento
        eventPublisher.publishEvent(new TriageCreatedEvent(savedTriage));

        return triageMapper.toResponseDTO(savedTriage);
    }

    /**
     * Obtiene todos los triages
     */
    @Transactional(readOnly = true)
    public List<TriageResponseDTO> getAllTriages() {
        return triageRepository.findAll().stream()
                .map(triageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un triage por ID
     */
    @Transactional(readOnly = true)
    public TriageResponseDTO getTriageById(Long id) {
        logger.debug("Buscando triage con ID: {}", id);
        Triage triage = triageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triage no encontrado con ID: " + id));
        return triageMapper.toResponseDTO(triage);
    }

    /**
     * Actualiza un triage
     */
    public TriageResponseDTO updateTriage(Long id, TriageUpdateDTO dto) {
        logger.info("Actualizando triage con ID: {}", id);

        Triage triage = triageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triage no encontrado con ID: " + id));

        triageMapper.updateEntityFromDTO(dto, triage);
        Triage updatedTriage = triageRepository.save(triage);
        logger.info("Triage actualizado exitosamente: {}", updatedTriage.getId());
        return triageMapper.toResponseDTO(updatedTriage);
    }

    /**
     * Elimina un triage
     */
    public void deleteTriage(Long id) {
        logger.info("Eliminando triage con ID: {}", id);

        if (!triageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Triage no encontrado con ID: " + id);
        }
        triageRepository.deleteById(id);
        logger.info("Triage eliminado exitosamente");
    }

    /**
     * Obtiene triages por paciente
     */
    @Transactional(readOnly = true)
    public List<TriageResponseDTO> getTriagesByPatient(Long patientId) {
        return triageRepository.findByPatientId(patientId).stream()
                .map(triageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene triages por estado
     */
    @Transactional(readOnly = true)
    public List<TriageResponseDTO> getTriagesByStatus(TriageStatus status) {
        return triageRepository.findByStatus(status).stream()
                .map(triageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un triage
     */
    public TriageResponseDTO updateTriageStatus(Long id, TriageStatus status) {
        logger.info("Actualizando estado de triage ID: {} a {}", id, status);

        Triage triage = triageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triage no encontrado con ID: " + id));

        triage.setStatus(status);
        Triage updatedTriage = triageRepository.save(triage);
        logger.info("Estado de triage actualizado exitosamente");
        return triageMapper.toResponseDTO(updatedTriage);
    }
}