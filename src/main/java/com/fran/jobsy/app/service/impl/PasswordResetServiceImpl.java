package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.entity.PasswordResetToken;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.PasswordResetTokenRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.MailService;
import com.fran.jobsy.app.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public void sendResetLink(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con ese correo"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .used(false)
                .build();
        tokenRepository.save(resetToken);

        String link = "https://jobsy-two.vercel.app/reset-password?token=" + token;
        String subject = "Restablecer contraseña - Jobsy";
        String body = """
                Hola,
                
                Recibimos una solicitud para restablecer tu contraseña en Jobsy.
                Puedes hacerlo haciendo clic en el siguiente enlace (válido por 15 minutos):
                
                %s
                
                Si no solicitaste este cambio, ignora este correo.
                """.formatted(link);
        mailService.sendEmail(email, subject, body);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido"));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token es inválido o ha expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}

