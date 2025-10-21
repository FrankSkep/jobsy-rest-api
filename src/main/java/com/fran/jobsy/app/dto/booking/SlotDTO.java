package com.fran.jobsy.app.dto.booking;

public record SlotDTO(
        String start,
        String end,
        boolean available) {
}
