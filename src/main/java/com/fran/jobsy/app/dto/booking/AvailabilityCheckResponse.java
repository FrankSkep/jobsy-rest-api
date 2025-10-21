package com.fran.jobsy.app.dto.booking;

public record AvailabilityCheckResponse(
        boolean available,
        String message) {
}