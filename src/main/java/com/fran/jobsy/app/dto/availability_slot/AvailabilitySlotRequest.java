package com.fran.jobsy.app.dto.availability_slot;

public record AvailabilitySlotRequest(
        Integer weekday,
        // 1=Monday, 7=Sunday
        String startTime,
        // HH:mm
        String endTime
) {
}
