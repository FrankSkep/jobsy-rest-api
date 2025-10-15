package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.provider_request.ProviderProfileRequest;
import com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO;
import com.fran.jobsy.app.entity.ProviderDocument;
import com.fran.jobsy.app.entity.ProviderRequest;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.ProviderStatus;
import com.fran.jobsy.app.exception.custom.CloudinaryException;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.repository.ProviderRequestRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.ProviderRequestService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProviderRequestServiceImpl implements ProviderRequestService {

    private final ProviderRequestRepository providerRequestRepository;
    private final CloudinaryService cloudinaryService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional
    public void applyForProvider(ProviderProfileRequest request, List<MultipartFile> documents) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        if (existsPendingRequestForUser(userId)) {
            throw new ConflictException("Ya existe una solicitud pendiente para este usuario.");
        }

        User userRef = authenticatedUserProvider.getUserReference(userId);

        ProviderRequest providerRequest = ProviderRequest.builder()
                .user(userRef)
                .status(ProviderStatus.PENDING)
                .bio(request.bio())
                .hourlyRate(request.hourlyRate())
                .yearsExperience(request.yearsExperience())
                .addressText(request.addressText())
                .lat(request.lat())
                .lng(request.lng())
                .serviceRadiusKm(request.serviceRadiusKm())
                .rfcHomoclave(request.rfcHomoclave())
                .verifiedCert(request.verifiedCert())
                .build();

        List<ProviderDocument> uploadedDocs = new ArrayList<>();
        List<String> uploadedIds = new ArrayList<>();

        try {
            for (MultipartFile file : documents) {
                Map<String, Object> uploadResult = cloudinaryService.upload(file);
                String documentId = (String) uploadResult.get("public_id");
                String documentUrl = (String) uploadResult.get("url");
                uploadedIds.add(documentId);

                ProviderDocument doc = ProviderDocument.builder()
                        .publicId(documentId)
                        .url(documentUrl)
                        .providerRequest(providerRequest)
                        .build();

                uploadedDocs.add(doc);
            }

            providerRequest.setDocuments(uploadedDocs);
            providerRequestRepository.save(providerRequest);

        } catch (
                Exception e) {
            rollbackUploads(uploadedIds);
        }
    }

    @Override
    public List<ProviderRequestDTO> getAllProviderRequests() {
        return providerRequestRepository.findAllProviderRequests();
    }

    private Boolean existsPendingRequestForUser(Long userId) {
        return providerRequestRepository.existsByUserIdAndStatus(userId, ProviderStatus.PENDING);
    }

    private void rollbackUploads(List<String> uploadedIds) {
        try {
            for (String publicId : uploadedIds) {
                cloudinaryService.delete(publicId);
            }
        } catch (
                Exception ex) {
            throw new CloudinaryException("Failed to rollback uploads: " + ex.getMessage());
        }
    }
}
