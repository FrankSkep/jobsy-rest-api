package com.fran.jobsy.app.dto.auth;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}