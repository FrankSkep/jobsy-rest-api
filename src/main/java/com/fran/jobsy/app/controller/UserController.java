package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.user.UserDTO;
import com.fran.jobsy.app.dto.user.UserFullDTO;
import com.fran.jobsy.app.dto.user.UserPublicDTO;
import com.fran.jobsy.app.dto.user.UserRequest;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.service.OfferingService;
import com.fran.jobsy.app.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Users", description = "Operations related to user management")
public class UserController {

    private final UserService userService;
    private final OfferingService offeringService;

    // General User Endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPublicDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/me/basic")
    public ResponseEntity<Void> updateMyBasicInfo(
            @Parameter(description = "User's username")
            @Valid @RequestBody UserRequest userRequest) {
        userService.updateUser(userRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "User's ID") @PathVariable Long id,
            @RequestBody Role role) {
        userService.updateRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@Parameter(description = "User's ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //=== Authenticated User Endpoints ===//
    @GetMapping("/me")
    public ResponseEntity<UserFullDTO> getMyInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<OfferingDTO>> getUserServices(@PathVariable Long id) {
        return ResponseEntity.ok(offeringService.getServicesByUserId(id));
    }
}