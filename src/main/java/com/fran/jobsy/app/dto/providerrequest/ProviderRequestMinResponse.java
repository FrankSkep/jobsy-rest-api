package com.fran.jobsy.app.dto.providerrequest;

import java.time.LocalDateTime;

public record ProviderRequestMinResponse(
        Long id,
        String userName,
        String userPhoto,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
