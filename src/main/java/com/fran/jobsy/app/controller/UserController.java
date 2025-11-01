package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.auth.PasswordRequestADMIN;
import com.fran.jobsy.app.dto.auth.PasswordUpdateRequest;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    // General User Endpoints
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Obtener todos los usuarios", description = "Solo accesible para ADMIN. Devuelve la lista de todos los usuarios registrados.")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve la información pública de un usuario por su ID.")
    public ResponseEntity<UserPublicResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Actualizar rol de usuario", description = "Solo accesible para SUPER_ADMIN y ADMIN. Permite cambiar el rol de un usuario.")
    public ResponseEntity<Void> updateRole(
            @PathVariable Long id,
            @RequestBody Role role) {
        userService.updateRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Solo accesible para ADMIN. Elimina un usuario por su ID.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Actualizar contraseña de usuario", description = "Solo accesible para ADMIN. Permite al administrador actualizar la contraseña de un usuario.")
    public ResponseEntity<Void> setPasswordByAdmin(
            @PathVariable Long id,
            @RequestBody PasswordRequestADMIN passwordRequest) {
        userService.setPasswordByAdmin(id, passwordRequest.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password-update")
    @Operation
    public ResponseEntity<Void> updateMyPassword(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        userService.updatePassword(passwordUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    //=== Authenticated User Endpoints ===//
    @GetMapping("/me/full")
    @Operation(summary = "Obtener mi información completa", description = "Devuelve la información completa del usuario autenticado.")
    public ResponseEntity<UserFullResponse> getMyFullInfo() {
        return ResponseEntity.ok(userService.getMyFullInfo());
    }

    @GetMapping("/me/basic")
    @Operation(summary = "Obtener mi información básica", description = "Devuelve la información básica del usuario autenticado.")
    public ResponseEntity<UserResponse> getMyBasicInfo() {
        return ResponseEntity.ok(userService.getMyBasicInfo());
    }

    @DeleteMapping("/me")
    @Operation(summary = "Eliminar mi cuenta", description = "Permite al usuario autenticado eliminar su propia cuenta.")
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/basic")
    @Operation(summary = "Actualizar mi información básica", description = "Permite al usuario autenticado actualizar su información básica.")
    public ResponseEntity<Void> updateMyBasicInfo(
            @RequestBody @Valid UserPatchRequest userPatchRequest) {
        userService.updateUser(userPatchRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/full")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Actualizar mi información completa", description = "Permite al usuario autenticado actualizar su información completa. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<Void> updateMyFullInfo(
            @RequestBody @Valid UserFullPatchRequest userFullDTO) {
        userService.updateUser(userFullDTO);
        return ResponseEntity.noContent().build();
    }
}
