package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.CertificationDTO;
import com.fran.jobsy.app.dto.CertificationRequest;
import com.fran.jobsy.app.entity.Certification;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.exception.custom.AuthenticationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.CertificationRepository;
import com.fran.jobsy.app.service.CertificationService;
import com.fran.jobsy.app.utils.AuthenticatedUserProvider;
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
    public CertificationDTO addCertification(CertificationRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        boolean exists = certificationRepository
                .existsByUserIdAndNameIgnoreCaseAndIssuerIgnoreCaseAndYear(
                        user.getId(),
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
                .user(user)
                .name(request.name().trim())
                .issuer(request.issuer().trim())
                .year(request.year())
                .build();

        Certification saved = certificationRepository.save(certification);
        return new CertificationDTO(saved.getId(), saved.getName(), saved.getIssuer(), saved.getYear());
    }

    @Override
    public void deleteCertification(Long certId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Certification certification = certificationRepository.findByIdAndUserId(certId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Certificación no encontrada."));

        certificationRepository.delete(certification);
    }

    @Override
    public List<CertificationDTO> getMyCertifications() {
        return certificationRepository.findAllByUserId(authenticatedUserProvider.getAuthenticatedUser().getId());
    }

    @Override
    public CertificationDTO updateCertification(Long id, CertificationRequest request) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificacion con id " + id + "no encontrada."));

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUser().getId();

        if (authenticatedUserId != cert.getUser().getId()) {
            throw new AuthenticationException("Certificacion con id " + id + "no encontrada.");
        }

        cert.setName(request.name());
        cert.setIssuer(request.issuer());
        cert.setYear(request.year());

        certificationRepository.save(cert);

        return new CertificationDTO(cert.getId(), cert.getName(),
                cert.getIssuer(), cert.getYear());
    }
}
