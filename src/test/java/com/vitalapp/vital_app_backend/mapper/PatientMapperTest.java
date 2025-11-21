package com.vitalapp.vital_app_backend.mapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vitalapp.vital_app_backend.dto.patient.PatientCreateDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientUpdateDTO;
import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.model.Patient;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Mapper Tests")
class PatientMapperTest {

    @InjectMocks
    private PatientMapper patientMapper;

    private PatientCreateDTO createDTO;
    private Patient patient;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        createDTO = PatientCreateDTO.builder()
                .fullName("Juan Pérez")
                .documentNumber("1234567890")
                .birthDate(LocalDate.of(1990, 5, 15))
                .phone("+573001234567")
                .address("Calle 123")
                .gender(Gender.MALE)
                .emergencyContact("María Pérez")
                .emergencyPhone("+573009876543")
                .build();

        patient = Patient.builder()
                .id(1L)
                .fullName("Juan Pérez")
                .documentNumber("1234567890")
                .birthDate(LocalDate.of(1990, 5, 15))
                .phone("+573001234567")
                .address("Calle 123")
                .gender(Gender.MALE)
                .emergencyContact("María Pérez")
                .emergencyPhone("+573009876543")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Debe convertir CreateDTO a Entity correctamente")
    void toEntity_shouldMapAllFields() {
        // When
        Patient result = patientMapper.toEntity(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(createDTO.getFullName(), result.getFullName());
        assertEquals(createDTO.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(createDTO.getBirthDate(), result.getBirthDate());
        assertEquals(createDTO.getPhone(), result.getPhone());
        assertEquals(createDTO.getAddress(), result.getAddress());
        assertEquals(createDTO.getGender(), result.getGender());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Debe retornar null cuando CreateDTO es null")
    void toEntity_shouldReturnNullWhenDTOIsNull() {
        // When
        Patient result = patientMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe convertir Entity a ResponseDTO correctamente")
    void toResponseDTO_shouldMapAllFieldsIncludingAge() {
        // When
        PatientResponseDTO result = patientMapper.toResponseDTO(patient);

        // Then
        assertNotNull(result);
        assertEquals(patient.getId(), result.getId());
        assertEquals(patient.getFullName(), result.getFullName());
        assertEquals(patient.getDocumentNumber(), result.getDocumentNumber());
        assertTrue(result.getAge() > 0); // Debe calcular edad
        assertEquals(patient.getPhone(), result.getPhone());
        assertEquals(patient.getAddress(), result.getAddress());
        assertEquals(patient.getGender(), result.getGender());
        assertEquals(patient.getEmergencyContact(), result.getEmergencyContact());
        assertEquals(patient.getEmergencyPhone(), result.getEmergencyPhone());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Debe retornar null cuando Entity es null")
    void toResponseDTO_shouldReturnNullWhenEntityIsNull() {
        // When
        PatientResponseDTO result = patientMapper.toResponseDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe actualizar solo campos no nulos de UpdateDTO")
    void updateEntityFromDTO_shouldUpdateOnlyNonNullFields() {
        // Given
        PatientUpdateDTO updateDTO = PatientUpdateDTO.builder()
                .fullName("Juan Pérez Actualizado")
                .phone("+573001111111")
                .build();

        String originalAddress = patient.getAddress();

        // When
        patientMapper.updateEntityFromDTO(updateDTO, patient);

        // Then
        assertEquals("Juan Pérez Actualizado", patient.getFullName());
        assertEquals("+573001111111", patient.getPhone());
        assertEquals(originalAddress, patient.getAddress()); // No debe cambiar
    }

    @Test
    @DisplayName("No debe hacer nada cuando UpdateDTO es null")
    void updateEntityFromDTO_shouldDoNothingWhenDTOIsNull() {
        // Given
        String originalName = patient.getFullName();

        // When
        patientMapper.updateEntityFromDTO(null, patient);

        // Then
        assertEquals(originalName, patient.getFullName());
    }

    @Test
    @DisplayName("No debe hacer nada cuando Entity es null")
    void updateEntityFromDTO_shouldDoNothingWhenEntityIsNull() {
        // Given
        PatientUpdateDTO updateDTO = PatientUpdateDTO.builder()
                .fullName("Nuevo Nombre")
                .build();

        // When & Then
        assertDoesNotThrow(() -> patientMapper.updateEntityFromDTO(updateDTO, null));
    }

    @Test
    @DisplayName("Debe actualizar todos los campos cuando todos son no nulos")
    void updateEntityFromDTO_shouldUpdateAllFieldsWhenAllNonNull() {
        // Given
        PatientUpdateDTO updateDTO = PatientUpdateDTO.builder()
                .fullName("Nuevo Nombre")
                .birthDate(LocalDate.of(1985, 3, 10))
                .phone("+573002222222")
                .address("Nueva Dirección")
                .emergencyContact("Nuevo Contacto")
                .emergencyPhone("+573003333333")
                .active(false)
                .build();

        // When
        patientMapper.updateEntityFromDTO(updateDTO, patient);

        // Then
        assertEquals("Nuevo Nombre", patient.getFullName());
        assertEquals(LocalDate.of(1985, 3, 10), patient.getBirthDate());
        assertEquals("+573002222222", patient.getPhone());
        assertEquals("Nueva Dirección", patient.getAddress());
        assertEquals("Nuevo Contacto", patient.getEmergencyContact());
        assertEquals("+573003333333", patient.getEmergencyPhone());
        assertFalse(patient.isActive());
    }
}