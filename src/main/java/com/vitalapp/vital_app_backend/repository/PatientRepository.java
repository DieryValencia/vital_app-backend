package com.vitalapp.vital_app_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vitalapp.vital_app_backend.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>,
                                           JpaSpecificationExecutor<Patient> {

    /**
     * Busca paciente por número de documento
     */
    Optional<Patient> findByDocumentNumber(String documentNumber);

    /**
     * Verifica si existe un paciente con el número de documento dado
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * Busca pacientes activos
     */
    List<Patient> findByActiveTrue();

    /**
     * Busca pacientes por nombre (contiene, ignorando mayúsculas/minúsculas)
     */
    List<Patient> findByFullNameContainingIgnoreCase(String name);

    /**
     * Busca paciente por número de documento y activo
     */
    Optional<Patient> findByDocumentNumberAndActiveTrue(String documentNumber);
}