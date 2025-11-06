package com.fran.jobsy.app.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserPublicResponse(
        String firstname,
        String lastname,
        String phone,
        String profilePhotoUrl,
        List<UserWorkPhotoResponse> workPhotos,
        String country,
        String bio,
        String addressText,
        Double averageRating,
        Integer totalReviews,
        Double serviceRadiusKm,
        LocalDateTime createdAt
) {
}
