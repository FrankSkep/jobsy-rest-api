package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.auth.AuthResponse;
import com.fran.jobsy.app.dto.auth.LoginRequest;
import com.fran.jobsy.app.dto.auth.RegisterRequest;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.auth.AuthenticationException;
import com.fran.jobsy.app.exception.auth.UserNotFoundException;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.AuthService;
import com.fran.jobsy.app.security.jwt.JwtService;
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
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserDetails user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found."));
            String token = jwtService.getToken(user);
            return AuthResponse.builder()
                    .token(token)
                    .build();
        } catch (
                Exception e) {
            throw new AuthenticationException("Incorrect user or password.");
        }
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("User already exists.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .country(request.getCountry())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }
}