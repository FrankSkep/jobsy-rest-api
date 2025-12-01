package com.fran.jobsy.app.dto.booking;

import com.fran.jobsy.app.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BookingStatusUpdateRequest(
        @NotNull BookingStatus status,
        @Size(max = 500) String comment,
        @Positive Double finalPrice
) {
}

