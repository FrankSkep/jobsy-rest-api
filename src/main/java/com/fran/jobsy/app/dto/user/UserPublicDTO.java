package com.fran.jobsy.app.dto.user;

import lombok.Builder;

@Builder
public record UserPublicDTO(
        String firstname,
        String lastname,
        String profilePhotoUrl,
        String country,
        String bio,
        Double hourlyRate,
        Integer yearsExperience,
        String addressText,
        Double serviceRadiusKm
) {}
