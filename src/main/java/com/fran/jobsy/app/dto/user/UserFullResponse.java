package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

public record UserFullResponse(
        Long id,
        String email,
        String lastname,
        String firstname,
        String country,
        String phone,
        Role role,
        UserPhotoResponse userPhoto,
        String bio,
        String avgRatingCache,
        String addressText,
        Double lat,
        Double lng,
        Double serviceRadiusKm,
        String rfcHomoclave,
        String curp
) {
}
