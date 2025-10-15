package com.fran.jobsy.app.dto.offering;

public record OfferingRequest(
        String category,
        String title,
        String description,
        Double basePrice
) {
}
