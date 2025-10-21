package com.fran.jobsy.app.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record BookingRequest(
        @NotNull
        Long providerId,

        @NotNull
        Long offeringId,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime startsAt,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime endsAt,

        @NotNull
        @PositiveOrZero
        Double priceAtBooking,

        @NotBlank
        String addressText,

        @NotNull
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double lat,

        @NotNull
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double lng
) {
}
