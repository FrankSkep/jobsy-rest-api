package com.fran.jobsy.app.dto.offering;

import java.util.List;

public record OfferingResponse(
        Long id,
        UserMinimalResponse user,
        String category,
        String title,
        String description,
        Double basePrice,
        Double yearsOfExperience,
        boolean isActive,
        Double averageRating,
        Integer totalReviews,
        List<String> photoUrls,
        String slug
) {
}
