package com.fran.jobsy.app.dto.booking;

import com.fran.jobsy.app.dto.offering.OfferingSummaryDTO;
import com.fran.jobsy.app.dto.user.UserSummaryDTO;
import com.fran.jobsy.app.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponseDTO(
        Long id,
        UserSummaryDTO clientId,
        UserSummaryDTO providerId,
        OfferingSummaryDTO offeringId,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        BookingStatus bookingStatus,
        Double priceAtBooking,
        String addressText,
        Double lat,
        Double lng
) {
}
