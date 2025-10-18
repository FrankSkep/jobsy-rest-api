package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.auth.AuthResponse;
import com.fran.jobsy.app.dto.auth.LoginRequest;
import com.fran.jobsy.app.dto.auth.RegisterRequest;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.AuthenticationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.security.jwt.JwtService;
import com.fran.jobsy.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            UserDetails user = userRepository.findByUsername(request.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found."));
            String token = jwtService.getToken(user);
            return new AuthResponse(token);
        } catch (
                Exception e) {
            throw new AuthenticationException("Incorrect user or password.");
        }
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.email())) {
            throw new AuthenticationException("User already exists.");
        }

        User user = User.builder()
                .username(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstname(request.firstname())
                .lastname(request.lastname())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return new AuthResponse(jwtService.getToken(user));
    }
}