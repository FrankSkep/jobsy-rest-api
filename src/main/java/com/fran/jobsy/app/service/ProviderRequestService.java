package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.provider_request.ProviderProfileRequest;
import com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProviderRequestService {

    void applyForProvider(ProviderProfileRequest request, List<MultipartFile> documents);

    Page<ProviderRequestDTO> getAllProviderRequests(Pageable pageable);

    void approve(Long requestId);

    void reject(Long requestId, String reason);
}
