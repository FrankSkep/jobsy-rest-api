package com.fran.jobsy.app.dto.offering;

import com.fran.jobsy.app.dto.category.CategoryDTO;

public record OfferingRequest(
        CategoryDTO category,
        String title,
        String description,
        Double basePrice
) {
}
