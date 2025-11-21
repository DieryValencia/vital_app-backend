package com.vitalapp.vital_app_backend.specification;

import com.vitalapp.vital_app_backend.model.Gender;
import com.vitalapp.vital_app_backend.model.Patient;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientSpecification {

    public static Specification<Patient> filterBy(
            String fullName,
            String documentNumber,
            String phone,
            Gender gender,
            LocalDate birthDateFrom,
            LocalDate birthDateTo,
            Boolean active) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nombre completo (case insensitive, búsqueda parcial)
            if (fullName != null && !fullName.isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")),
                    "%" + fullName.toLowerCase() + "%"
                ));
            }

            // Filtro por número de documento (exacto)
            if (documentNumber != null && !documentNumber.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                    root.get("documentNumber"),
                    documentNumber
                ));
            }

            // Filtro por teléfono (búsqueda parcial)
            if (phone != null && !phone.isBlank()) {
                predicates.add(criteriaBuilder.like(
                    root.get("phone"),
                    "%" + phone + "%"
                ));
            }

            // Filtro por género
            if (gender != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("gender"),
                    gender
                ));
            }

            // Filtro por rango de fecha de nacimiento
            if (birthDateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("birthDate"),
                    birthDateFrom
                ));
            }

            if (birthDateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("birthDate"),
                    birthDateTo
                ));
            }

            // Filtro por estado activo
            if (active != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("active"),
                    active
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}