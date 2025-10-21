package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.booking.AvailabilityCheckResponse;
import com.fran.jobsy.app.dto.booking.DailyAvailabilityResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ProviderAvailabilityService {
    DailyAvailabilityResponse getAvailabilityForDay(Long providerId, LocalDate date);

    AvailabilityCheckResponse checkDayAvailability(Long providerId, LocalDate date);

    AvailabilityCheckResponse checkAvailability(Long providerId, LocalDateTime startsAt, LocalDateTime endsAt);
}
