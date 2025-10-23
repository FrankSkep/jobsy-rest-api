package com.fran.jobsy.app.dto.review;

import jakarta.validation.constraints.*;

public record ReviewRequest(
        @NotNull @Positive
        Long clientId,

        @NotNull @Positive
        Long providerId,

        @NotNull @Min(1) @Max(5)
        Integer rating,
        // 1-5

        @Size(max = 1000)
        String comment
) {
}