package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponseDTO;
import com.fran.jobsy.app.dto.booking.BookingStatusUpdateReqDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequest bookingReq);

    List<BookingListDTO> getClientBookings();

    List<BookingListDTO> getProviderBookings();

    BookingResponseDTO getBooking(Long id);

    BookingResponseDTO updateBookingStatus(Long id, BookingStatusUpdateReqDTO statusUpdateReqDTO);
}
