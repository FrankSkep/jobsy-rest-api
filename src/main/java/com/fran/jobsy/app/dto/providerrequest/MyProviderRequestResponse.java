package com.fran.jobsy.app.dto.providerrequest;

import java.time.LocalDateTime;

public record MyProviderRequestResponse(
        Long id,
        String status,
        String rejectionReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
