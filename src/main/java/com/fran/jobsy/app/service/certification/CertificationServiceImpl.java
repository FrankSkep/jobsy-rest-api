package com.fran.jobsy.app.service.certification;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.CertificationRequest;
import com.fran.jobsy.app.dto.CertificationResponse;
import com.fran.jobsy.app.entity.Certification;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.exception.custom.UnauthorizedAccessException;
import com.fran.jobsy.app.repository.CertificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CertificationRepository certificationRepository;

    @Override
    @Transactional
    public CertificationResponse addCertification(CertificationRequest request) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        boolean exists = certificationRepository
                .existsByUserIdAndNameIgnoreCaseAndIssuerIgnoreCaseAndYear(
                        userId,
                        request.name().trim(),
                        request.issuer().trim(),
                        request.year()
                );
        if (exists) {
            throw new IllegalStateException("Ya existe una certificación igual para este usuario.");
        }

        if (request.year() != null) {
            int current = Year.now().getValue();
            if (request.year() > current) {
                throw new IllegalArgumentException("El año no puede ser mayor al actual.");
            }
        }

        Certification certification = Certification.builder()
                .user(authenticatedUserProvider.getUserReference(userId))
                .name(request.name().trim())
                .issuer(request.issuer().trim())
                .year(request.year())
                .build();

        Certification saved = certificationRepository.save(certification);
        return new CertificationResponse(saved.getId(), saved.getName(), saved.getIssuer(), saved.getYear());
    }

    @Override
    public void deleteCertification(Long certId) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        Certification certification = certificationRepository.findByIdAndUserId(certId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Certificación no encontrada."));

        certificationRepository.delete(certification);
    }

    @Override
    public List<CertificationResponse> getUserCertifications(Long id) {
        return certificationRepository.findAllByUserId(id);
    }

    @Override
    public CertificationResponse updateCertification(Long id, CertificationRequest request) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificacion con id " + id + "no encontrada."));

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        if (authenticatedUserId != cert.getUser().getId()) {
            throw new UnauthorizedAccessException("El usuario autenticado no es el propietario de la certificación.");
        }

        cert.setName(request.name());
        cert.setIssuer(request.issuer());
        cert.setYear(request.year());

        certificationRepository.save(cert);

        return new CertificationResponse(cert.getId(), cert.getName(),
                cert.getIssuer(), cert.getYear());
    }
}
