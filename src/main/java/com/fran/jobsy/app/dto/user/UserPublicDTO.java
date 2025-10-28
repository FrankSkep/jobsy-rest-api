package com.fran.jobsy.app.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record UserPublicDTO(
        String firstname,
        String lastname,
        String phone,
        String profilePhotoUrl,
        List<UserWorkPhotoResponse> workPhotos,
        String country,
        String bio,
        String addressText,
        Double serviceRadiusKm
) {
}
