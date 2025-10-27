package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

public record UserResponse(
        Long id,
        String email,
        String lastname,
        String firstname,
        String phone,
        String country,
        Role role
) {
}