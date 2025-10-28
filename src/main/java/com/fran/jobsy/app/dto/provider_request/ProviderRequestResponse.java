package com.fran.jobsy.app.dto.provider_request;

import com.fran.jobsy.app.dto.user.UserSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ProviderRequestResponse(
        Long id,
        UserSummaryResponse user,
        String bio,
        String addressText,
        String rfcHomoclave,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProviderDocumentResponse> documents
) {
}
