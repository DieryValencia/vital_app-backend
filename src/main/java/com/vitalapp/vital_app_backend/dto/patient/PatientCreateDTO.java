package com.vitalapp.vital_app_backend.dto.patient;

import java.time.LocalDate;

import com.vitalapp.vital_app_backend.model.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreateDTO {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "El nombre completo solo puede contener letras y espacios")
    private String fullName;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 5, max = 20, message = "El número de documento debe tener entre 5 y 20 caracteres")
    private String documentNumber;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate birthDate;

    @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "El teléfono debe tener un formato válido")
    private String phone;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String address;

    @NotNull(message = "El género es obligatorio")
    private Gender gender;

    @Size(max = 100, message = "El contacto de emergencia no puede exceder 100 caracteres")
    private String emergencyContact;

    @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "El teléfono de emergencia debe tener un formato válido")
    private String emergencyPhone;
}