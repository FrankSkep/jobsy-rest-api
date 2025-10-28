package com.fran.jobsy.app.dto.availabilityslot;

import java.time.LocalTime;

public record AvailabilitySlotResponse(
        Long id,
        Long providerId,
        Integer weekday,
// 1=Monday, 7=Sunday
        LocalTime startTime,
// "09:00"
        LocalTime endTime
// "17:00"
) {
}
