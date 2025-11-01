package com.fran.jobsy.app.dto.user;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UserFullPatchRequest(
        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        String addressText,

        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double lat,

        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        Double lng,

        @DecimalMin(value = "0.1", message = "Service radius must be at least 0.1 km")
        @DecimalMax(value = "100.0", message = "Service radius must not exceed 100 km")
        Double serviceRadiusKm
) {
}
