package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponse;
import com.fran.jobsy.app.dto.booking.BookingStatusUpdateRequest;
import com.fran.jobsy.app.dto.booking.BookingSummaryResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest bookingReq);

    List<BookingSummaryResponse> getClientBookings();

    List<BookingSummaryResponse> getProviderBookings();

    BookingResponse getBooking(Long id);

    BookingResponse updateBookingStatus(Long id, BookingStatusUpdateRequest statusUpdateReqDTO);
}
