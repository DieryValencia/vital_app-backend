package com.vitalapp.vital_app_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.Period;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "document_number", unique = true)
    private String documentNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "age")
    @Builder.Default
    private int age = 0;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    // Getter personalizado para calcular edad
    public int getAge() {
        if (this.age == 0 && this.birthDate != null) {
            return Period.between(this.birthDate, LocalDate.now()).getYears();
        }
        return this.age;
    }
}