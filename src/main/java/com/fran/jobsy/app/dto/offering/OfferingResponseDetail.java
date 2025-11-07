package com.fran.jobsy.app.dto.offering;

import com.fran.jobsy.app.dto.offeringphoto.OfferingPhotoResponse;

import java.util.List;

public record OfferingResponseDetail(
        Long id,
        UserMinimalResponse user,
        List<OfferingPhotoResponse> photos,
        String category,
        String title,
        String description,
        Double basePrice,
        Integer yearsOfExperience,
        boolean isActive
) {
}
