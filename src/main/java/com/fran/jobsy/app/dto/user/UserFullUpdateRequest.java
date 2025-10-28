package com.fran.jobsy.app.dto.user;

import jakarta.validation.constraints.*;

public record UserFullUpdateRequest(
        @NotBlank(message = "Bio is mandatory")
        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio,

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
        Double serviceRadiusKm
) {
}
