package com.fran.jobsy.app.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
        @NotBlank(message = "First name is mandatory")
        String firstname,

        @NotBlank(message = "Last name is mandatory")
        String lastname,

        @NotBlank(message = "Country is mandatory")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Country must contain only letters and spaces")
        String country,

        @NotBlank(message = "Phone is mandatory")
        @Pattern(regexp = "^\\d{10,14}$", message = "Phone must contain between 10 and 14 digits")
        String phone

) {
}
