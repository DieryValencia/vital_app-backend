package com.vitalapp.vital_app_backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro de doctores.
 * Permite a los administradores crear cuentas de doctor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDoctorRequest {

    /**
     * Nombre de usuario único para el doctor
     */
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;

    /**
     * Correo electrónico único del doctor
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    /**
     * Contraseña para la cuenta del doctor
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    /**
     * Nombre del doctor
     */
    private String firstName;

    /**
     * Apellido del doctor
     */
    private String lastName;
}