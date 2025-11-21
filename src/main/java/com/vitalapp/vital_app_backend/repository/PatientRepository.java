package com.vitalapp.vital_app_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT p FROM Patient p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Patient> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Busca paciente por número de documento y activo
     */
    Optional<Patient> findByDocumentNumberAndActiveTrue(String documentNumber);

    /**
     * Cuenta pacientes activos
     */
    long countByActiveTrue();
}