package com.fran.jobsy.app.common;

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

    public Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationException("No hay un usuario autenticado en el contexto.");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }

        throw new AuthenticationException("El principal no es un User válido.");
    }

    public User getAuthenticatedUser() {
        return userRepository.findById(getAuthenticatedUserId())
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));
    }

    public User getAuthenticatedUserReference() {
        return userRepository.getReferenceById(getAuthenticatedUserId());
    }

    public User getUserReference(Long userId) {
        return userRepository.getReferenceById(userId);
    }
}
