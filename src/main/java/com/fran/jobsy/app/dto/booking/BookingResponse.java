package com.fran.jobsy.app.dto.booking;

import com.fran.jobsy.app.dto.offering.OfferingSummaryResponse;
import com.fran.jobsy.app.dto.user.UserSummaryResponse;
import com.fran.jobsy.app.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        UserSummaryResponse client,
        UserSummaryResponse provider,
        OfferingSummaryResponse offering,
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
