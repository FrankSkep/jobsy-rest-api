package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

public record UserFullResponse(
        Long id,
        String email,
        String lastname,
        String firstname,
        UserPhotoResponse userPhoto,
        String country,
        Role role,
        String bio,
        Double hourlyRate,
        Integer yearsExperience,
        String addressText,
        Double lat,
        Double lng,
        Double serviceRadiusKm,
        Boolean verifiedCert
) {
    public UserFullResponse {
        if (verifiedCert == null) {
            verifiedCert = false;
        }
    }
}
