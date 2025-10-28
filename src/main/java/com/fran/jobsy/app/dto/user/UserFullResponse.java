package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

public record UserFullResponse(
        Long id,
        String email,
        String lastname,
        String firstname,
        String phone,
        UserPhotoResponse userPhoto,
        String country,
        Role role,
        String bio,
        String addressText,
        Double lat,
        Double lng,
        Double serviceRadiusKm
) {
}
