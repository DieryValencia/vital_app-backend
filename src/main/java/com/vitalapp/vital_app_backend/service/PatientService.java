package com.vitalapp.vital_app_backend.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.vitalapp.vital_app_backend.exception.custom.DuplicateResourceException;
import com.vitalapp.vital_app_backend.exception.custom.ResourceNotFoundException;
import com.vitalapp.vital_app_backend.mapper.PatientMapper;
import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.model.Patient;
import com.vitalapp.vital_app_backend.repository.AppointmentRepository;
import com.vitalapp.vital_app_backend.repository.PatientRepository;
import com.vitalapp.vital_app_backend.repository.TriageRepository;
import com.vitalapp.vital_app_backend.specification.PatientSpecification;

/**
 * Servicio de negocio para la gestión de pacientes en el sistema VitalApp.
 *
 * Esta clase proporciona toda la lógica de negocio relacionada con pacientes,
 * incluyendo operaciones CRUD, búsqueda avanzada, filtrado y validaciones.
 * Utiliza transacciones para garantizar la integridad de los datos.
 *
 * @author Equipo VitalApp
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    /**
     * Repositorio para acceder a los datos de pacientes en la base de datos.
     * Inyectado automáticamente por Spring.
     */
    @Autowired
    private PatientRepository patientRepository;

    /**
     * Repositorio para acceder a los datos de citas.
     */
    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Repositorio para acceder a los datos de triajes.
     */
    @Autowired
    private TriageRepository triageRepository;

    /**
     * Mapper para convertir entre entidades Patient y DTOs.
     * Inyectado automáticamente por Spring.
     */
    @Autowired
    private PatientMapper patientMapper;

    /**
     * Crea un nuevo paciente en el sistema.
     *
     * Valida que no exista un paciente con el mismo número de documento,
     * calcula la edad basada en la fecha de nacimiento y marca al paciente
     * como activo por defecto.
     *
     * @param dto Datos del paciente a crear
     * @return DTO de respuesta con la información del paciente creado
     * @throws DuplicateResourceException si ya existe un paciente con el mismo documento
     */
    public PatientResponseDTO createPatient(PatientCreateDTO dto) {
        logger.info("Creando paciente con documento: {}", dto.getDocumentNumber());

        if (patientRepository.existsByDocumentNumber(dto.getDocumentNumber())) {
            throw new DuplicateResourceException("Ya existe un paciente con el documento: " + dto.getDocumentNumber());
        }

        Patient patient = patientMapper.toEntity(dto);

        // FORZAR cálculo de edad
        calculateAndSetAge(patient);
        patient.setActive(true);

        logger.info("Guardando paciente: {} con edad {}", patient.getFullName(), patient.getAge());

        Patient savedPatient = patientRepository.save(patient);

        logger.info("Paciente creado exitosamente con ID: {}", savedPatient.getId());

        return patientMapper.toResponseDTO(savedPatient);
    }

    /**
     * Obtiene todos los pacientes con paginación, ordenamiento y filtros.
     *
     * Este método permite recuperar una lista paginada de pacientes aplicando
     * diversos filtros opcionales como nombre, documento, teléfono, género,
     * fecha de nacimiento y estado activo. También soporta ordenamiento
     * personalizado por cualquier campo válido.
     *
     * @param page Número de página (0-based)
     * @param size Tamaño de página
     * @param sortBy Campo por el cual ordenar
     * @param sortDirection Dirección del ordenamiento (ASC o DESC)
     * @param fullName Filtro por nombre completo (opcional)
     * @param documentNumber Filtro por número de documento (opcional)
     * @param phone Filtro por teléfono (opcional)
     * @param gender Filtro por género (opcional)
     * @param birthDateFrom Fecha de nacimiento desde (opcional)
     * @param birthDateTo Fecha de nacimiento hasta (opcional)
     * @param active Filtro por estado activo (opcional)
     * @return Página de pacientes con metadatos de paginación
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
        logger.debug("Buscando paciente con ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        logger.debug("Paciente encontrado: {}", patient.getFullName());
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
        logger.info("Actualizando paciente con ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        patientMapper.updateEntityFromDTO(dto, patient);

        // FORZAR cálculo de edad
        calculateAndSetAge(patient);

        Patient updatedPatient = patientRepository.save(patient);
        logger.info("Paciente actualizado exitosamente: {}", updatedPatient.getFullName());
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
     * Elimina un paciente físicamente junto con sus citas y triajes asociados
     */
    public void deletePatient(Long id) {
        logger.info("Eliminando paciente con ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        // Eliminar citas asociadas
        appointmentRepository.deleteAll(appointmentRepository.findByPatientId(id));
        logger.debug("Citas asociadas eliminadas para paciente ID: {}", id);

        // Eliminar triajes asociados
        triageRepository.deleteAll(triageRepository.findByPatientId(id));
        logger.debug("Triajes asociados eliminados para paciente ID: {}", id);

        // Eliminar el paciente
        patientRepository.deleteById(id);
        logger.info("Paciente eliminado exitosamente: {}", patient.getFullName());
    }

    // Método auxiliar para calcular edad
    private void calculateAndSetAge(Patient patient) {
        if (patient.getBirthDate() != null) {
            int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
            patient.setAge(age);
        } else {
            patient.setAge(0);
        }
    }
}