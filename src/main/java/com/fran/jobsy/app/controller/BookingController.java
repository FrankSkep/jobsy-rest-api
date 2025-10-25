package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponseDTO;
import com.fran.jobsy.app.dto.booking.BookingStatusUpdateReqDTO;
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
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody @Valid BookingRequest bookingRequest) {
        BookingResponseDTO bookingResponseDTO = bookingService.createBooking(bookingRequest);
        URI location = RestUtils.buildCreatedLocation(bookingResponseDTO.id());
        return ResponseEntity.created(location).body(bookingResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @GetMapping("/client")
    public ResponseEntity<List<BookingListDTO>> getClientBookings() {
        return ResponseEntity.ok(bookingService.getClientBookings());
    }

    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<List<BookingListDTO>> getProviderBookings() {
        return ResponseEntity.ok(bookingService.getProviderBookings());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody @Valid BookingStatusUpdateReqDTO updateStatusReq) {
        BookingResponseDTO updatedBooking = bookingService.updateBookingStatus(id, updateStatusReq);
        return ResponseEntity.ok(updatedBooking);
    }
}
