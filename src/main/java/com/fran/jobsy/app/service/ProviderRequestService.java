package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.provider_request.ProviderApplyRequest;
import com.fran.jobsy.app.dto.provider_request.ProviderRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProviderRequestService {

    void applyForProvider(ProviderApplyRequest request, List<MultipartFile> documents);

    Page<ProviderRequestResponse> getAllProviderRequests(Pageable pageable);

    void approve(Long requestId);

    void reject(Long requestId, String reason);

    ProviderRequestResponse getMyProviderRequest();
}
