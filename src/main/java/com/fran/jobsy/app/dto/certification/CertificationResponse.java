package com.fran.jobsy.app.dto;

public record CertificationResponse(
        Long id,
        String name,
        String issuer,
        Integer year
) {
}
