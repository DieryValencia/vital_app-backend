package com.vitalapp.vital_app_backend.mapper;

import org.springframework.stereotype.Component;

import com.vitalapp.vital_app_backend.dto.patient.PatientCreateDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientResponseDTO;
import com.vitalapp.vital_app_backend.dto.patient.PatientUpdateDTO;
import com.vitalapp.vital_app_backend.model.Patient;

@Component
public class PatientMapper {

    /**
     * Convierte un PatientCreateDTO a una entidad Patient
     */
    public Patient toEntity(PatientCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Patient.builder()
                .fullName(dto.getFullName())
                .documentNumber(dto.getDocumentNumber())
                .birthDate(dto.getBirthDate())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .gender(dto.getGender())
                .emergencyContact(dto.getEmergencyContact())
                .emergencyPhone(dto.getEmergencyPhone())
                .build();
    }

    /**
     * Convierte una entidad Patient a PatientResponseDTO
     */
    public PatientResponseDTO toResponseDTO(Patient patient) {
        if (patient == null) {
            return null;
        }

        return PatientResponseDTO.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .documentNumber(patient.getDocumentNumber())
                .birthDate(patient.getBirthDate())
                .age(patient.getAge())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .gender(patient.getGender())
                .emergencyContact(patient.getEmergencyContact())
                .emergencyPhone(patient.getEmergencyPhone())
                .active(patient.isActive())
                .build();
    }

    /**
     * Actualiza una entidad Patient con los datos de PatientUpdateDTO
     */
    public void updateEntityFromDTO(PatientUpdateDTO dto, Patient patient) {
        if (dto == null || patient == null) {
            return;
        }

        if (dto.getFullName() != null) {
            patient.setFullName(dto.getFullName());
        }
        if (dto.getBirthDate() != null) {
            patient.setBirthDate(dto.getBirthDate());
        }
        if (dto.getPhone() != null) {
            patient.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            patient.setAddress(dto.getAddress());
        }
        if (dto.getEmergencyContact() != null) {
            patient.setEmergencyContact(dto.getEmergencyContact());
        }
        if (dto.getEmergencyPhone() != null) {
            patient.setEmergencyPhone(dto.getEmergencyPhone());
        }
        if (dto.getActive() != null) {
            patient.setActive(dto.getActive());
        }
    }
}