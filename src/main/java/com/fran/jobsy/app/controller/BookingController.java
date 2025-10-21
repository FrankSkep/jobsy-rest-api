package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponseDTO;
import com.fran.jobsy.app.service.BookingService;
import com.fran.jobsy.app.util.RestUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody @Valid BookingRequest bookingRequest) {
        BookingResponseDTO bookingResponseDTO = bookingService.createBooking(bookingRequest);
        URI location = RestUtils.buildCreatedLocation(bookingResponseDTO.id());
        return ResponseEntity.created(location).body(bookingResponseDTO);
    }

    @GetMapping("/client")
    public ResponseEntity<List<BookingListDTO>> getClientBookings() {
        return ResponseEntity.ok(bookingService.getClientBookings());
    }
}
