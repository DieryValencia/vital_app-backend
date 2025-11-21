package com.vitalapp.vital_app_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.model.Patient;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Patient Repository Integration Tests")
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();

        patient1 = Patient.builder()
                .fullName("Juan Pérez")
                .documentNumber("1234567890")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .active(true)
                .build();

        patient2 = Patient.builder()
                .fullName("María García")
                .documentNumber("0987654321")
                .birthDate(LocalDate.of(1985, 10, 20))
                .gender(Gender.FEMALE)
                .active(false)
                .build();
    }

    @Test
    @DisplayName("Debe guardar paciente correctamente")
    void save_shouldPersistPatient() {
        // When
        Patient saved = patientRepository.save(patient1);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Juan Pérez", saved.getFullName());
    }

    @Test
    @DisplayName("Debe encontrar paciente por documento")
    void findByDocumentNumber_shouldReturnPatient() {
        // Given
        patientRepository.save(patient1);

        // When
        Optional<Patient> found = patientRepository.findByDocumentNumber("1234567890");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Juan Pérez", found.get().getFullName());
    }

    @Test
    @DisplayName("Debe retornar vacío si documento no existe")
    void findByDocumentNumber_shouldReturnEmptyWhenNotFound() {
        // When
        Optional<Patient> found = patientRepository.findByDocumentNumber("9999999999");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Debe verificar si documento existe")
    void existsByDocumentNumber_shouldReturnTrue() {
        // Given
        patientRepository.save(patient1);

        // When
        boolean exists = patientRepository.existsByDocumentNumber("1234567890");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe retornar false si documento no existe")
    void existsByDocumentNumber_shouldReturnFalseWhenNotExists() {
        // When
        boolean exists = patientRepository.existsByDocumentNumber("9999999999");

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("Debe encontrar solo pacientes activos")
    void findByActiveTrue_shouldReturnOnlyActivePatients() {
        // Given
        patientRepository.save(patient1); // activo
        patientRepository.save(patient2); // inactivo

        // When
        List<Patient> activePatients = patientRepository.findByActiveTrue();

        // Then
        assertEquals(1, activePatients.size());
        assertTrue(activePatients.get(0).isActive());
    }

    @Test
    @DisplayName("Debe buscar pacientes por nombre (ignorando mayúsculas)")
    void findByFullNameContainingIgnoreCase_shouldReturnMatches() {
        // Given
        patientRepository.save(patient1);
        patientRepository.save(patient2);

        // When
        List<Patient> found = patientRepository.findByFullNameContainingIgnoreCase("juan");

        // Then
        assertEquals(1, found.size());
        assertEquals("Juan Pérez", found.get(0).getFullName());
    }

    @Test
    @DisplayName("Debe buscar pacientes por nombre parcial")
    void findByFullNameContainingIgnoreCase_shouldReturnPartialMatches() {
        // Given
        patientRepository.save(patient1);
        patientRepository.save(patient2);

        // When
        List<Patient> found = patientRepository.findByFullNameContainingIgnoreCase("Pérez");

        // Then
        assertEquals(1, found.size());
        assertEquals("Juan Pérez", found.get(0).getFullName());
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay coincidencias")
    void findByFullNameContainingIgnoreCase_shouldReturnEmptyWhenNoMatches() {
        // Given
        patientRepository.save(patient1);

        // When
        List<Patient> found = patientRepository.findByFullNameContainingIgnoreCase("inexistente");

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Debe encontrar paciente por documento y activo")
    void findByDocumentNumberAndActiveTrue_shouldReturnActivePatient() {
        // Given
        patientRepository.save(patient1); // activo
        patientRepository.save(patient2); // inactivo

        // When
        Optional<Patient> found = patientRepository.findByDocumentNumberAndActiveTrue("1234567890");

        // Then
        assertTrue(found.isPresent());
        assertTrue(found.get().isActive());
    }

    @Test
    @DisplayName("Debe retornar vacío si paciente existe pero no está activo")
    void findByDocumentNumberAndActiveTrue_shouldReturnEmptyWhenInactive() {
        // Given
        patientRepository.save(patient2); // inactivo

        // When
        Optional<Patient> found = patientRepository.findByDocumentNumberAndActiveTrue("0987654321");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Debe manejar múltiples pacientes con diferentes estados")
    void multiplePatients_shouldHandleDifferentStates() {
        // Given
        Patient patient3 = Patient.builder()
                .fullName("Carlos López")
                .documentNumber("1111111111")
                .birthDate(LocalDate.of(1975, 3, 10))
                .gender(Gender.MALE)
                .active(true)
                .build();

        patientRepository.save(patient1); // activo
        patientRepository.save(patient2); // inactivo
        patientRepository.save(patient3); // activo

        // When
        List<Patient> activePatients = patientRepository.findByActiveTrue();
        List<Patient> allPatients = patientRepository.findAll();

        // Then
        assertEquals(2, activePatients.size());
        assertEquals(3, allPatients.size());
        assertThat(activePatients).allMatch(Patient::isActive);
    }
}