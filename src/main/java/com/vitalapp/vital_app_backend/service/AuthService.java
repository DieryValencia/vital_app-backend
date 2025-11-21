package com.vitalapp.vital_app_backend.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitalapp.vital_app_backend.config.JwtService;
import com.vitalapp.vital_app_backend.dto.auth.AuthResponseDTO;
import com.vitalapp.vital_app_backend.dto.auth.LoginRequestDTO;
import com.vitalapp.vital_app_backend.dto.auth.RefreshTokenRequestDTO;
import com.vitalapp.vital_app_backend.dto.auth.RegisterRequestDTO;
import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role("USER")
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role("USER")
                .build();
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String newToken = jwtService.generateToken(userDetails);

                return AuthResponseDTO.builder()
                        .token(newToken)
                        .refreshToken(refreshToken)
                        .type("Bearer")
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role("USER")
                        .build();
            }
        }

        throw new RuntimeException("Refresh token inválido");
    }
}