package com.fran.jobsy.app.dto.provider_request;

import com.fran.jobsy.app.dto.user.UserSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ProviderRequestResponseDTO(
        Long id,
        UserSummaryResponse user,
        String bio,
        Integer yearsExperience,
        String addressText,
        Double lat,
        Double lng,
        Double serviceRadiusKm,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProviderDocumentResponse> documents
) {
}
