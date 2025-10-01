package com.fran.jobsy.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CertificationRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 160, message = "Nombre demasiado largo")
        String name,

        @NotBlank(message = "El emisor es obligatorio")
        @Size(max = 160, message = "Emisor demasiado largo")
        String issuer,

        @Min(value = 1950, message = "El año no puede ser menor a 1950")
        Integer year
) {
}
