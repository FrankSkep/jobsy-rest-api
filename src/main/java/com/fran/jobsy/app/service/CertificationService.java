package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.CertificationDTO;
import com.fran.jobsy.app.dto.CertificationRequest;

import java.util.List;

public interface CertificationService {
    CertificationDTO addCertification(CertificationRequest request);

    void deleteCertification(Long certId);

    List<CertificationDTO> getMyCertifications();
}
