package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

public record UserFullDTO(
        Long id,
        String username,
        String lastname,
        String firstname,
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
    public UserFullDTO {
        if (verifiedCert == null) {
            verifiedCert = false;
        }
    }
}
