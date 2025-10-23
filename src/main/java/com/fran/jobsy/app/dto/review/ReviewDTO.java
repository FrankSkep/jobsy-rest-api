package com.fran.jobsy.app.dto.review;

import com.fran.jobsy.app.dto.booking.BookingListDTO;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        BookingListDTO booking,
        // this include Client and Provider names
        Long clientId,
        Long providerId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
