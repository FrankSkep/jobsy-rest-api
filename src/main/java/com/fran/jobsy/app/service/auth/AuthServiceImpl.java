package com.fran.jobsy.app.service.auth;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fran.jobsy.app.config.security.jwt.JwtService;
import com.fran.jobsy.app.dto.auth.LoginRequest;
import com.fran.jobsy.app.dto.auth.RefreshTokenRequest;
import com.fran.jobsy.app.dto.auth.RegisterRequest;
import com.fran.jobsy.app.dto.auth.TokenResponse;
import com.fran.jobsy.app.entity.RefreshToken;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.AuthenticationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            User user = (User) userRepository.findByUsername(request.email())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

            String accessToken = jwtService.getToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return new TokenResponse(accessToken, refreshToken.getToken());
        } catch (
                Exception e) {
            throw new AuthenticationException("Usuario o contraseña incorrectos.");
        }
    }

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.email())) {
            throw new AuthenticationException("El correo ya está en uso.");
        }

        User user = User.builder()
                .username(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstname(request.firstname())
                .lastname(request.lastname())
                .role(Role.USER)
                .slug(generateUniqueSlug(request.firstname(), request.lastname()))
                .build();

        userRepository.save(user);

        String accessToken = jwtService.getToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken.getToken());
    }

    public TokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.getToken(user);

        return new TokenResponse(newAccessToken, refreshToken.getToken());
    }

    // generate slug with uniqueness check
    private String generateUniqueSlug(String firstname, String lastname) {
        String slug = generateSlug(firstname, lastname);

        if (userRepository.existsBySlug(slug)) {
            // if slug exists, generate a new one with a longer unique ID
            String baseSlug = (firstname + "-" + lastname)
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("^-+|-+$", "");

            char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            String uniqueId = NanoIdUtils.randomNanoId(
                    NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                    alphabet,
                    10
            );

            slug = baseSlug + "-" + uniqueId;
        }

        return slug;
    }

    // generate slug
    private String generateSlug(String firstname, String lastname) {
        String baseSlug = (firstname + "-" + lastname)
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        String uniqueId = NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                alphabet,
                8
        );

        return baseSlug + "-" + uniqueId;
    }
}