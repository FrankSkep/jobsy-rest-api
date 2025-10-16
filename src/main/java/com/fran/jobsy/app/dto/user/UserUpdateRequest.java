package com.fran.jobsy.app.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
        @NotBlank(message = "First name is mandatory")
        String firstname,

        @Pattern(regexp = "^.{0,}$", message = "Last name must be a string")
        String lastname,

        @Pattern(regexp = "(^$)|(^[A-Za-z ]+$)", message = "Country must contain only letters and spaces")
        String country,

        @Pattern(regexp = "(^$)|(^\\d{10,14}$)", message = "Phone must contain between 10 and 14 digits")
        String phone

) {
}
