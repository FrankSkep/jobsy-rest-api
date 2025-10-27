package com.fran.jobsy.app.dto.booking;

public record SlotResponse(
        String start,
        String end,
        boolean available) {
}
