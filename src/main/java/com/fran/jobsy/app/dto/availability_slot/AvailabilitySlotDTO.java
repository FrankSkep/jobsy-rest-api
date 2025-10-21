package com.fran.jobsy.app.dto.availability_slot;

public record AvailabilitySlotDTO(
        Long id,
        Long providerId,
        Integer weekday,
// 1=Monday, 7=Sunday
        String startTime,
// "09:00"
        String endTime
// "17:00"
) {
}
