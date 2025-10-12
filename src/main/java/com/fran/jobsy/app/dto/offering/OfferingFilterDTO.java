package com.fran.jobsy.app.dto.offering;

public record OfferingFilterDTO(
        Long categoryId,
        Double minPrice,
        Double maxPrice,
        Double minRating,
        Double lat,
        Double lng,
        Double radiusKm,
        String location
) {
}