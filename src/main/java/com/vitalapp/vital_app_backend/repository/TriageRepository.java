package com.vitalapp.vital_app_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vitalapp.vital_app_backend.model.Triage;
import com.vitalapp.vital_app_backend.model.TriageStatus;

@Repository
public interface TriageRepository extends JpaRepository<Triage, Long> {

    /**
     * Busca triajes por paciente
     */
    List<Triage> findByPatientId(Long patientId);

    /**
     * Busca triajes por estado
     */
    List<Triage> findByStatus(TriageStatus status);

    /**
     * Busca triajes por paciente ordenados por fecha de creación descendente
     */
    List<Triage> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    /**
     * Busca triajes con nivel de severidad mayor o igual al especificado
     */
    List<Triage> findBySeverityLevelGreaterThanEqual(Integer level);
}