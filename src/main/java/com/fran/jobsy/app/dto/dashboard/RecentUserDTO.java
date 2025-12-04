package com.fran.jobsy.app.dto.dashboard;

import com.fran.jobsy.app.enums.Role;

import java.time.LocalDateTime;

public record RecentUserDTO(
        Long id,
        String firstname,
        String lastname,
        String email,
        Role role,
        String profileImageUrl,
        LocalDateTime createdAt
) {
}

