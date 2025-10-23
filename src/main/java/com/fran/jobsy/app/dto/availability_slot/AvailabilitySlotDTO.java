package com.fran.jobsy.app.dto.availability_slot;

import java.time.LocalTime;

public record AvailabilitySlotDTO(
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
