package com.fran.jobsy.app.dto.review;

import java.time.LocalDateTime;

public record ReviewSummaryDTO(
        Long id,
        Integer rating,
        String comment,
        String clientName,
        String offeringTitle,
        LocalDateTime createdAt
) {
}
