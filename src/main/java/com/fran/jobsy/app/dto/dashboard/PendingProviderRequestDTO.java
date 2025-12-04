package com.fran.jobsy.app.dto.dashboard;

import java.time.LocalDateTime;

public record PendingProviderRequestDTO(
        Long id,
        Long userId,
        String userFullName,
        String status,
        LocalDateTime createdAt,
        String bio
) {
}

