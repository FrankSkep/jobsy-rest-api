package com.fran.jobsy.app.dto.provider_request;

import com.fran.jobsy.app.dto.user.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record ProviderRequestDTO(
        Long id,
        UserSummaryDTO user,
        String bio,
        Double hourlyRate,
        Integer yearsExperience,
        String addressText,
        Double lat,
        Double lng,
        Double serviceRadiusKm,
        Boolean verifiedCert,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProviderDocumentDTO> documents
) {
}
