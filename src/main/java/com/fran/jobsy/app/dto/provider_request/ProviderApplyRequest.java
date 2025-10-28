package com.fran.jobsy.app.dto.provider_request;

import jakarta.validation.constraints.*;

public record ProviderApplyRequest(
        @NotBlank(message = "Bio is mandatory")
        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio,

        @NotNull(message = "Years of experience is mandatory")
        @Min(value = 0, message = "Years of experience cannot be negative")
        @Max(value = 50, message = "Years of experience must not exceed 50")
        Integer yearsExperience,

        @NotBlank(message = "Address is mandatory")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String addressText,

        @NotNull(message = "Latitude is mandatory")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double lat,

        @NotNull(message = "Longitude is mandatory")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        Double lng,

        @NotNull(message = "Service radius is mandatory")
        @DecimalMin(value = "0.1", message = "Service radius must be at least 0.1 km")
        @DecimalMax(value = "100.0", message = "Service radius must not exceed 100 km")
        Double serviceRadiusKm,

        @NotBlank(message = "RFC homoclave es obligatorio")
        @Pattern(regexp = "^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$", message = "RFC homoclave inválido")
        String rfcHomoclave
) {
}
