package com.fran.jobsy.app.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record UserPublicDTO(
        String firstname,
        String lastname,
        String profilePhotoUrl,
        List<UserWorkPhotoDTO> workPhotos,
        String country,
        String bio,
        Double hourlyRate,
        Integer yearsExperience,
        String addressText,
        Double serviceRadiusKm
) {
}
