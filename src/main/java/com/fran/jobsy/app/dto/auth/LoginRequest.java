package com.fran.jobsy.app.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Enter a valid email")
        String email,

        @NotBlank(message = "Enter a valid password")
        String password
) {
}