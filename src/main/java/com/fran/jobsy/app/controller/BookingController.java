package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponse;
import com.fran.jobsy.app.dto.booking.BookingStatusUpdateRequest;
import com.fran.jobsy.app.dto.booking.BookingSummaryResponse;
import com.fran.jobsy.app.service.BookingService;
import com.fran.jobsy.app.util.RestUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bookings", description = "Operaciones relacionadas con las reservas de un servicio")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingRequest bookingRequest) {
        BookingResponse bookingResponse = bookingService.createBooking(bookingRequest);
        URI location = RestUtils.buildCreatedLocation(bookingResponse.id());
        return ResponseEntity.created(location).body(bookingResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @GetMapping("/client")
    public ResponseEntity<List<BookingSummaryResponse>> getClientBookings() {
        return ResponseEntity.ok(bookingService.getClientBookings());
    }

    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<List<BookingSummaryResponse>> getProviderBookings() {
        return ResponseEntity.ok(bookingService.getProviderBookings());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody @Valid BookingStatusUpdateRequest updateStatusReq) {
        BookingResponse updatedBooking = bookingService.updateBookingStatus(id, updateStatusReq);
        return ResponseEntity.ok(updatedBooking);
    }
}
