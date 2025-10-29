package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

public record UserResponse(
        Long id,
        String email,
        Role role,
        String firstname,
        String lastname,
        String country,
        String phone
) {
}