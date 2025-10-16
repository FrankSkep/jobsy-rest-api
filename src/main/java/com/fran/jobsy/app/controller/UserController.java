package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.user.UserDTO;
import com.fran.jobsy.app.dto.user.UserFullDTO;
import com.fran.jobsy.app.dto.user.UserPublicDTO;
import com.fran.jobsy.app.dto.user.UserRequest;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.service.OfferingService;
import com.fran.jobsy.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones relacionadas con la gestión de usuarios")
public class UserController {

    private final UserService userService;
    private final OfferingService offeringService;

    // General User Endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los usuarios", description = "Solo accesible para ADMIN. Devuelve la lista de todos los usuarios registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo ADMIN")
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve la información pública de un usuario por su ID.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    public ResponseEntity<UserPublicDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/me/basic")
    @Operation(summary = "Actualizar mi información básica", description = "Permite al usuario autenticado actualizar su información básica.")
    @ApiResponse(responseCode = "204", description = "Información actualizada correctamente")
    public ResponseEntity<Void> updateMyBasicInfo(
            @Parameter(description = "Nombre de usuario")
            @Valid @RequestBody UserRequest userRequest) {
        userService.updateUser(userRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar rol de usuario", description = "Solo accesible para ADMIN. Permite cambiar el rol de un usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rol actualizado correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo ADMIN")
    })
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @RequestBody Role role) {
        userService.updateRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Solo accesible para ADMIN. Elimina un usuario por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo ADMIN")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //=== Authenticated User Endpoints ===//
    @GetMapping("/me")
    @Operation(summary = "Obtener mi información completa", description = "Devuelve la información completa del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Información obtenida correctamente")
    public ResponseEntity<UserFullDTO> getMyInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @DeleteMapping("/me")
    @Operation(summary = "Eliminar mi cuenta", description = "Permite al usuario autenticado eliminar su propia cuenta.")
    @ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente")
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/services")
    @Operation(summary = "Obtener servicios de usuario", description = "Devuelve la lista de servicios ofrecidos por un usuario público.")
    @ApiResponse(responseCode = "200", description = "Lista de servicios obtenida correctamente")
    public ResponseEntity<List<OfferingDTO>> getUserServices(@PathVariable Long id) {
        return ResponseEntity.ok(offeringService.getServicesByUserId(id));
    }
}