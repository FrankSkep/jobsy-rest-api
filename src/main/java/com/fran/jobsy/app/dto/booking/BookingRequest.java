package com.fran.jobsy.app.dto.booking;

public record BookingRequest(
        Long providerId,
        Long offeringId,
        String startsAt,
        String endsAt,
        Double priceAtBooking,
        String addressText,
        Double lat,
        Double lng
) {
}