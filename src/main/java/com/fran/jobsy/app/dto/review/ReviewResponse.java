package com.fran.jobsy.app.dto.review;

import com.fran.jobsy.app.dto.booking.BookingSummaryResponse;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        BookingSummaryResponse booking,
        // this include Client and Provider names
        Long clientId,
        Long providerId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
