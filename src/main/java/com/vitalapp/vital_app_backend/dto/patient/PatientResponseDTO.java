package com.vitalapp.vital_app_backend.dto.patient;

import java.time.LocalDate;

import com.vitalapp.vital_app_backend.model.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {

    private Long id;
    private String fullName;
    private String documentNumber;
    private LocalDate birthDate;
    private int age;
    private String phone;
    private String address;
    private Gender gender;
    private String emergencyContact;
    private String emergencyPhone;
    private boolean active;
}