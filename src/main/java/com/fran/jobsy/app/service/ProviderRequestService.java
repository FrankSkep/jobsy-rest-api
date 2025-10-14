package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.provider_request.ProviderProfileRequest;
import com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProviderRequestService {

    void applyForProvider(ProviderProfileRequest request, List<MultipartFile> documents);

    List<ProviderRequestDTO> getAllProviderRequests();
}
