package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        Role role,
        String firstname,
        String lastname,
        String country,
        String phone,
        LocalDateTime createdAt,
        String profileImageUrl
) {
}