package com.fran.jobsy.app.util;

import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.exception.custom.AuthenticationException;
import com.fran.jobsy.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    /**
     * Obtiene el ID del usuario directamente del JWT (sin consulta a BD)
     */
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("No hay un usuario autenticado en el contexto.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }

        throw new IllegalStateException("El principal no es una instancia de CustomUserDetails.");
    }

    /**
     * Obtiene el User completo solo cuando sea necesario (hace consulta a BD)
     * Úsalo solo cuando realmente necesites los datos completos del usuario
     */
    public User getAuthenticatedUser() {
        Long userId = getAuthenticatedUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));
    }

    /**
     * Para relaciones entre entidades - uso más eficiente con reference
     */
    public User getAuthenticatedUserReference() {
        Long userId = getAuthenticatedUserId();
        return userRepository.getReferenceById(userId);
    }

    /**
     * Obtiene una referencia de cualquier usuario (útil para relaciones)
     */
    public User getUserReference(Long userId) {
        return userRepository.getReferenceById(userId);
    }
}