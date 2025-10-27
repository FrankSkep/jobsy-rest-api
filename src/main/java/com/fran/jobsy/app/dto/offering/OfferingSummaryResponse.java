package com.fran.jobsy.app.dto.offering;

public record OfferingSummaryResponse(
        Long id,
        String category,
        String title,
        Double basePrice
) {
}
