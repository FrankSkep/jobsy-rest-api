package com.fran.jobsy.app.dto.availability_slot;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record AvailabilitySlotRequest(
        @NotNull(message = "El día de la semana es obligatorio")
        @Min(value = 1, message = "El día de la semana debe ser entre 1 y 7")
        @Max(value = 7, message = "El día de la semana debe ser entre 1 y 7")
        Integer weekday,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime startTime,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime endTime
) {
    @AssertTrue(message = "La hora de inicio debe ser anterior a la hora de fin")
    public boolean isStartTimeBeforeEndTime() {
        if (startTime == null || endTime == null)
            return true;
        return startTime.isBefore(endTime);
    }
}
