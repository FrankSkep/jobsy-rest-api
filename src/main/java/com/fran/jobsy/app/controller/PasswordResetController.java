package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.auth.PasswordResetDTO;
import com.fran.jobsy.app.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        passwordResetService.sendResetLink(email);
        return ResponseEntity.ok(Map.of("message", "Correo de restablecimiento enviado si el usuario existe."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetDTO passwordResetDTO) {
        passwordResetService.resetPassword(passwordResetDTO.token(), passwordResetDTO.newPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
    }
}
