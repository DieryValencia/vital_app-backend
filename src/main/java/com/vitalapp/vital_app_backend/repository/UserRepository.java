package com.vitalapp.vital_app_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vitalapp.vital_app_backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // JpaRepository<User, Long> significa:
    // - User: la entidad que maneja
    // - Long: el tipo del ID (cambió de String a Long)

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
