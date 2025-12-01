package com.fran.jobsy.app.dto.booking;

import com.fran.jobsy.app.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingSummaryResponse(
        Long id,
        String providerName,
        String clientName,
        String offeringTitle,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        BookingStatus status,
        Double priceAtBooking,
        Integer reviewRating  // null si no hay review
) {
}