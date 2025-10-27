package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.CertificationResponse;
import com.fran.jobsy.app.dto.CertificationRequest;

import java.util.List;

public interface CertificationService {
    CertificationResponse addCertification(CertificationRequest request);

    void deleteCertification(Long certId);

    List<CertificationResponse> getUserCertifications(Long id);

    CertificationResponse updateCertification(Long id, CertificationRequest request);
}
