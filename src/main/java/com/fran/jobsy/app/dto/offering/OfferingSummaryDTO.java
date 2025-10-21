package com.fran.jobsy.app.dto.offering;

public record OfferingSummaryDTO(
        Long id,
        String category,
        String title,
        Double basePrice
) {
}
