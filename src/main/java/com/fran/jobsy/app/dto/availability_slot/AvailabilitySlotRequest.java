package com.fran.jobsy.app.dto.availability_slot;

import jakarta.validation.constraints.*;

public record AvailabilitySlotRequest(
        @NotNull(message = "El día de la semana es obligatorio")
        @Min(value = 1, message = "El día de la semana debe ser entre 1 y 7")
        @Max(value = 7, message = "El día de la semana debe ser entre 1 y 7")
        Integer weekday,

        @NotNull(message = "La hora de inicio es obligatoria")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "La hora de inicio debe tener el formato HH:mm")
        String startTime,

        @NotNull(message = "La hora de fin es obligatoria")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "La hora de fin debe tener el formato HH:mm")
        String endTime
) {
    @AssertTrue(message = "La hora de inicio debe ser anterior a la hora de fin")
    public boolean isStartTimeBeforeEndTime() {
        if (startTime == null || endTime == null)
            return true; // handled by @NotNull
        try {
            java.time.LocalTime start = java.time.LocalTime.parse(startTime);
            java.time.LocalTime end = java.time.LocalTime.parse(endTime);
            return start.isBefore(end);
        } catch (
                Exception e) {
            return true; // handled by @Pattern
        }
    }
}
