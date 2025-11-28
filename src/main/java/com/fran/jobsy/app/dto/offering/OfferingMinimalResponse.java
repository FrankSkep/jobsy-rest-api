package com.fran.jobsy.app.dto.offering;

public record OfferingMinimalResponse(
        Long id,
        String title,
        String category,
        Double basePrice,
        boolean isActive,
        String photoUrl,
        Double lat,
        Double lng
) {
}
