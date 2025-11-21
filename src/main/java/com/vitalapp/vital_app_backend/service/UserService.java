package com.vitalapp.vital_app_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Crear usuario
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Buscar por ID (cambió String a Long)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Buscar por username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Verificar si existe username
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    // Eliminar usuario (cambió String a Long)
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}