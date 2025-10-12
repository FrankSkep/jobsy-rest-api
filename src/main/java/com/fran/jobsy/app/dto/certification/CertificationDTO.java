package com.fran.jobsy.app.dto;

public record CertificationDTO(
        Long id,
        String name,
        String issuer,
        Integer year
) {
}
