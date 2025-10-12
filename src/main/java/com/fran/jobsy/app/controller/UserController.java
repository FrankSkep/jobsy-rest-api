package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.user.*;
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

    @Operation(summary = "Update user", description = "Updates a user's data by their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/me/basic")
    public ResponseEntity<Void> updateMyBasicInfo(
            @Parameter(description = "User's username")
            @Valid @RequestBody UserRequest userRequest) {
        userService.updateUser(userRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update role", description = "Updates a user's role by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "User's ID") @PathVariable Long id,
            @RequestBody Role role) {
        userService.updateRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@Parameter(description = "User's ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //=== Authenticated User Endpoints ===//
    @Operation(summary = "Get authenticated user info", description = "Retrieves information about the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<UserFullDTO> getMyInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PutMapping("/me/provider")
    public ResponseEntity<Void> updateMyProviderProfile(@RequestBody @Valid ProviderProfileRequest providerProfileRequest) {
        userService.updateProviderInfo(providerProfileRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}/services")
    public ResponseEntity<List<OfferingDTO>> getUserServices(@PathVariable Long id) {
        return ResponseEntity.ok(offeringService.getServicesByUserId(id));
    }

//    @Operation(summary = "Update password", description = "Updates a user's password")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
//            @ApiResponse(responseCode = "400", description = "Incorrect old password"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @PutMapping("/{username}/password")
//    public void updatePassword(
//            @Parameter(description = "User's username") @PathVariable String username,
//            @RequestBody PasswordRequest password) {
//        userServiceImpl.updatePassword(username, password);
//    }
}