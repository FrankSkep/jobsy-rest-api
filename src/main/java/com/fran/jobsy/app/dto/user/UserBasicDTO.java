package com.fran.jobsy.app.dto.user;

public record UserBasicDTO(
        Long id,
        String firstname,
        String lastname,
        String country
) {
}
