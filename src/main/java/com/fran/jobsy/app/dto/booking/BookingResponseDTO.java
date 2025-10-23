package com.fran.jobsy.app.dto.booking;

import com.fran.jobsy.app.dto.offering.OfferingSummaryDTO;
import com.fran.jobsy.app.dto.user.UserSummaryDTO;
import com.fran.jobsy.app.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponseDTO(
        Long id,
        UserSummaryDTO client,
        UserSummaryDTO provider,
        OfferingSummaryDTO offering,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        BookingStatus bookingStatus,
        Double priceAtBooking,
        String addressText,
        Double lat,
        Double lng,
        String statusComment
) {
}
