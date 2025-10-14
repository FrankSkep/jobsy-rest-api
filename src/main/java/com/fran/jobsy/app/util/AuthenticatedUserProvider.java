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

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("No hay un usuario autenticado en el contexto.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        throw new IllegalStateException("El principal no es una instancia de User.");
    }

    public Long getAuthenticatedUserId() {
        User user = getAuthenticatedUser();
        return user.getId();
    }

    public User getUserReference(Long userId) {
        return userRepository.getReferenceById(userId);
    }
}