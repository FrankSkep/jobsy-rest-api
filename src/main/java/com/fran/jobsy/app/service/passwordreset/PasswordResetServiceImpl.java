package com.fran.jobsy.app.service.passwordreset;

import com.fran.jobsy.app.entity.PasswordResetToken;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.PasswordResetTokenRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final static String FRONTEND_RESET_URL = "https://jobsy-two.vercel.app/reset-password";

    @Transactional
    public void sendResetLink(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("Si existe una cuenta asociada a este correo, se ha enviado un enlace para restablecer la contraseña."));

        // invalidate existing tokens
        tokenRepository.findByUserAndUsedFalse(user)
                .forEach(token -> {
                    token.setUsed(true);
                    tokenRepository.save(token);
                });

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .used(false)
                .build();
        tokenRepository.save(resetToken);

        String link = FRONTEND_RESET_URL + "?token=" + token;
        String subject = "Restablecer contraseña - Jobsy";
        String body = """
                Hola %s,
                
                Recibimos una solicitud para restablecer tu contraseña en Jobsy.
                Puedes hacerlo haciendo clic en el siguiente enlace (válido por 15 minutos):
                
                %s
                
                Si no solicitaste este cambio, ignora este correo.
                
                Saludos,
                Equipo Jobsy
                """.formatted(user.getFirstname(), link);
        mailService.sendEmail(email, subject, body);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido"));

        if (resetToken.isUsed()) {
            throw new ConflictException("El token ya ha sido utilizado");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ConflictException("El token ha expirado");
        }

        User user = resetToken.getUser();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ConflictException("La nueva contraseña debe ser diferente a la actual");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}

