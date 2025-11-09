package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.auth.PasswordResetRequest;
import com.fran.jobsy.app.service.passwordreset.PasswordResetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "PasswordReset", description = "Operaciones para el restablecimiento de contraseñas de usuarios")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        passwordResetService.sendResetLink(email);
        return ResponseEntity.ok(Map.of("message", "Si existe una cuenta asociada a este correo, recibirás un enlace para restablecer la contraseña."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        passwordResetService.resetPassword(passwordResetRequest.token(), passwordResetRequest.newPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
    }
}
