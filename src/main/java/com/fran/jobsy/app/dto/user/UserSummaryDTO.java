package com.fran.jobsy.app.dto.user;

public record UserSummaryDTO(
        Long id,
        String fullName,
        String photoUrl,
        String country
) {
}
