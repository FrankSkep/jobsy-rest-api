package com.fran.jobsy.app.service.providerrequest;

import com.fran.jobsy.app.dto.providerrequest.ProviderApplyRequest;
import com.fran.jobsy.app.dto.providerrequest.ProviderRequestMinResponse;
import com.fran.jobsy.app.dto.providerrequest.ProviderRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProviderRequestService {

    void applyForProvider(ProviderApplyRequest request, List<MultipartFile> documents);

    Page<ProviderRequestMinResponse> getAllProviderRequests(Pageable pageable);

    void approve(Long requestId);

    void reject(Long requestId, String reason);

    ProviderRequestResponse getMyProviderRequest();

    ProviderRequestResponse getProviderRequestById(Long requestId);
}
