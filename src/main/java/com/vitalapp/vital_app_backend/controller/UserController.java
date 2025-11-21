package com.vitalapp.vital_app_backend.controller;

import com.vitalapp.vital_app_backend.model.User;
import com.vitalapp.vital_app_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;
    
    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Retorna la lista completa de usuarios registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token requerido"
        )
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Retorna la información detallada de un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = User.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
        @Parameter(description = "ID del usuario", example = "1")
        @PathVariable Long id
    ) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Crear nuevo usuario",
        description = "Registra un nuevo usuario en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = User.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PostMapping
    public ResponseEntity<User> createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del usuario a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = User.class))
        )
        @RequestBody User user
    ) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario del sistema (eliminación permanente)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID del usuario", example = "1")
        @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}