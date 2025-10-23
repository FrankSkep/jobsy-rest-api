package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.provider_request.ProviderDocumentDTO;
import com.fran.jobsy.app.dto.provider_request.ProviderProfileRequest;
import com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO;
import com.fran.jobsy.app.dto.user.UserSummaryDTO;
import com.fran.jobsy.app.entity.ProviderDocument;
import com.fran.jobsy.app.entity.ProviderRequest;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.NotificationType;
import com.fran.jobsy.app.enums.ProviderRequestStatus;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.CloudinaryException;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.exception.custom.ProviderApplicationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.ProviderRequestRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.ProviderRequestService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProviderRequestServiceImpl implements ProviderRequestService {

    private final ProviderRequestRepository providerRequestRepository;
    private final CloudinaryService cloudinaryService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final NotificationServiceImpl notificationService;

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
                .status(ProviderRequestStatus.PENDING)
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

            notificationService.notifyUser(userRef, "Jobsy | Solicitud de proveedor recibida",
                    "Tu solicitud para ser proveedor ha sido recibida y está pendiente de revisión.",
                    NotificationType.SYSTEM, true);

        } catch (
                Exception e) {
            rollbackUploads(uploadedIds);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderRequestDTO> getAllProviderRequests(Pageable pageable) {
        return providerRequestRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private ProviderRequestDTO toDTO(ProviderRequest pr) {
        return new ProviderRequestDTO(
                pr.getId(),
                new UserSummaryDTO(
                        pr.getUser().getId(),
                        pr.getUser().getFirstname(),
                        pr.getUser().getLastname(),
                        pr.getUser().getCountry()
                ),
                pr.getBio(),
                pr.getHourlyRate(),
                pr.getYearsExperience(),
                pr.getAddressText(),
                pr.getLat(),
                pr.getLng(),
                pr.getServiceRadiusKm(),
                pr.getVerifiedCert(),
                pr.getStatus().name(),
                pr.getCreatedAt(),
                pr.getUpdatedAt(),
                pr.getDocuments().stream()
                        .map(doc -> new ProviderDocumentDTO(doc.getId(), doc.getPublicId(), doc.getUrl()))
                        .toList()
        );
    }

    @Override
    @Transactional
    public void approve(Long requestId) {
        ProviderRequest request = providerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (request.getStatus() != ProviderRequestStatus.PENDING) {
            throw new ProviderApplicationException("Solo pueden aprobarse solicitudes pendientes");
        }

        User admin = authenticatedUserProvider.getAuthenticatedUserReference();

        // Update user data
        User user = request.getUser();
        user.setRole(Role.PROVIDER);
        user.setBio(request.getBio());
        user.setHourlyRate(request.getHourlyRate());
        user.setYearsExperience(request.getYearsExperience());
        user.setAddressText(request.getAddressText());
        user.setLat(request.getLat());
        user.setLng(request.getLng());
        user.setServiceRadiusKm(request.getServiceRadiusKm());
        user.setRfcHomoclave(request.getRfcHomoclave());
        user.setVerifiedCert(request.getVerifiedCert());

        // update request data
        request.setStatus(ProviderRequestStatus.APPROVED);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        request.setRejectionReason(null);

        providerRequestRepository.save(request);

        notificationService.notifyUser(user, "Jobsy - Solicitud de proveedor aprobada",
                "¡Felicidades! Tu solicitud para ser proveedor ha sido aprobada. Ya puedes ofrecer tus servicios en la plataforma.",
                NotificationType.SYSTEM, true);
    }


    @Override
    @Transactional
    public void reject(Long requestId, String reason) {
        ProviderRequest request = providerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (request.getStatus() != ProviderRequestStatus.PENDING) {
            throw new ProviderApplicationException("Solo pueden rechazarse solicitudes pendientes");
        }

        User admin = authenticatedUserProvider.getAuthenticatedUserReference();

        request.setStatus(ProviderRequestStatus.REJECTED);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        request.setRejectionReason(reason);

        providerRequestRepository.save(request);

        notificationService.notifyUser(request.getUser(), "Jobsy - Solicitud de proveedor rechazada",
                "Lamentamos informarte que tu solicitud para ser proveedor fue rechazada. Motivo: " + reason,
                NotificationType.SYSTEM, true);
    }


    private Boolean existsPendingRequestForUser(Long userId) {
        return providerRequestRepository.existsByUserIdAndStatus(userId, ProviderRequestStatus.PENDING);
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
