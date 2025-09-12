package com.fran.jobsy.app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPublicDTO {
    private String firstname;
    private String lastname;
    private String country;
    private String bio;
    private Double hourlyRate;
    private Integer yearsExperience;
    private String addressText;
    private Double serviceRadiusKm;
}
