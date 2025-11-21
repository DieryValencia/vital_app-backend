package com.vitalapp.vital_app_backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final PatientRepository patientRepository;

    /**
     * Verifica si el usuario autenticado tiene acceso al recurso del paciente
     * @param patientId ID del paciente
     * @param authentication Contexto de autenticación
     * @return true si el usuario tiene acceso
     */
    public boolean isOwner(Long patientId, Authentication authentication) {
        // Como eliminamos roles, todos los usuarios autenticados tienen acceso
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Obtiene el usuario actualmente autenticado
     * @return Usuario autenticado o null si no hay autenticación
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }
}