package com.fran.jobsy.app.dto.offering;

public record OfferingResponse(
        Long id,
        UserMinimalResponse user,
        String category,
        String title,
        String description,
        Double basePrice
) {
}
