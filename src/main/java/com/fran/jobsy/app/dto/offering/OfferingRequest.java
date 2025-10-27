package com.fran.jobsy.app.dto.offering;

import com.fran.jobsy.app.dto.category.CategoryResponse;

public record OfferingRequest(
        CategoryResponse category,
        String title,
        String description,
        Double basePrice
) {
}
