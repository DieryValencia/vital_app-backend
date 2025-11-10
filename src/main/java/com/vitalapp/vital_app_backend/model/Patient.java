package com.vitalapp.vital_app_backend.model;

import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que representa a un paciente en el sistema VitalApp.
 *
 * Esta clase mapea la tabla 'patients' en la base de datos y contiene
 * toda la información personal y médica básica de un paciente.
 * Incluye campos para contacto de emergencia y estado de actividad.
 *
 * @author Equipo VitalApp
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {

    /**
     * Identificador único del paciente.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del paciente.
     * Campo obligatorio con longitud máxima de 100 caracteres.
     */
    @Column(nullable = false, length = 100)
    private String fullName;

    /**
     * Número de documento de identidad del paciente.
     * Campo único y obligatorio con longitud máxima de 20 caracteres.
     */
    @Column(unique = true, nullable = false, length = 20)
    private String documentNumber;

    /**
     * Fecha de nacimiento del paciente.
     * Campo obligatorio utilizado para calcular la edad.
     */
    @Column(nullable = false)
    private LocalDate birthDate;

    /**
     * Número de teléfono del paciente.
     * Campo opcional con longitud máxima de 20 caracteres.
     */
    @Column(length = 20)
    private String phone;

    /**
     * Dirección de residencia del paciente.
     * Campo opcional con longitud máxima de 200 caracteres.
     */
    @Column(length = 200)
    private String address;

    /**
     * Género del paciente.
     * Campo obligatorio que utiliza un enum para valores predefinidos.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    /**
     * Nombre del contacto de emergencia.
     * Campo opcional con longitud máxima de 100 caracteres.
     */
    @Column(length = 100)
    private String emergencyContact;

    /**
     * Número de teléfono del contacto de emergencia.
     * Campo opcional con longitud máxima de 20 caracteres.
     */
    @Column(length = 20)
    private String emergencyPhone;

    /**
     * Estado de actividad del paciente.
     * Por defecto es true (activo). Los pacientes inactivos no se eliminan físicamente.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Método calculado que retorna la edad del paciente en años.
     * No se persiste en la base de datos, se calcula dinámicamente.
     *
     * @return Edad del paciente en años, o 0 si no hay fecha de nacimiento
     */
    @Transient
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}