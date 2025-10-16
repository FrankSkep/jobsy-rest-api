package com.fran.jobsy.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String username,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
        String password,

        @NotBlank(message = "First name is mandatory")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstname,

        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastname
) {
}
