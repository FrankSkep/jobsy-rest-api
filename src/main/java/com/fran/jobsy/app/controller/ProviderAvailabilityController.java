package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.booking.AvailabilityCheckResponse;
import com.fran.jobsy.app.dto.booking.DailyAvailabilityResponse;
import com.fran.jobsy.app.service.ProviderAvailabilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "ProviderAvailability", description = "Operaciones relacionadas con la disponibilidad de los proveedores")
public class ProviderAvailabilityController {

    private final ProviderAvailabilityService providerAvailabilityRepository;

    @GetMapping("/{providerId}/availability/check-day")
    public ResponseEntity<AvailabilityCheckResponse> checkDayAvailability(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        AvailabilityCheckResponse response = providerAvailabilityRepository.checkDayAvailability(providerId, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{providerId}/availability/day")
    public ResponseEntity<DailyAvailabilityResponse> getAvailabilityForDay(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DailyAvailabilityResponse response = providerAvailabilityRepository.getAvailabilityForDay(providerId, date);
        return ResponseEntity.ok(response);
    }
}
