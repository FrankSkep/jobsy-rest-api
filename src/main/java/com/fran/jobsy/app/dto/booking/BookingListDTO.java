package com.fran.jobsy.app.dto.booking;

import com.fran.jobsy.app.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingListDTO(
        Long id,
        String providerName,
        String clientName,
        String offeringTitle,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        BookingStatus status,
        Double priceAtBooking
) {
}