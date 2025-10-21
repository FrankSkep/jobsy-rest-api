package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingListDTO createBooking(BookingRequest bookingReq);

    List<BookingListDTO> getBookings();

    BookingResponseDTO getBooking(Long id);

    void confirmBooking(Long id);

    void cancelBooking(Long id);

    void completeBooking(Long id);
}
