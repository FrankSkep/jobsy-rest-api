package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFullDTO {
    private Long id;
    private String username;
    private String lastname;
    private String firstname;
    private String country;
    private Role role;

    // Provider-specific fields
    private String bio;
    private Double hourlyRate;
    private Integer yearsExperience;
    private String addressText;
    private Double lat;
    private Double lng;
    private Double serviceRadiusKm;
    private Boolean verifiedCert = false;
}
