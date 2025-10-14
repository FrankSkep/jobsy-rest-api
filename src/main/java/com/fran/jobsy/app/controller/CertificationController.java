package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.CertificationDTO;
import com.fran.jobsy.app.dto.CertificationRequest;
import com.fran.jobsy.app.service.CertificationService;
import com.fran.jobsy.app.util.RestUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("users/{id}/certifications")
    public ResponseEntity<List<CertificationDTO>> getMyCertifications(@PathVariable Long id) {
        List<CertificationDTO> certifications = certificationService.getUserCertifications(id);
        return ResponseEntity.ok(certifications);
    }

    @PostMapping("/users/me/certifications")
    public ResponseEntity<CertificationDTO> addCertification(@RequestBody @Valid CertificationRequest certificationRequest) {
        CertificationDTO certification = certificationService.addCertification(certificationRequest);
        URI location = RestUtils.buildCreatedLocation(certification.id());
        return ResponseEntity.created(location).body(certification);
    }

    @DeleteMapping("/users/me/certifications/{certId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certId) {
        certificationService.deleteCertification(certId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/me/certifications/{certId}")
    public ResponseEntity<CertificationDTO> updateCertification(@PathVariable Long certId, @RequestBody @Valid CertificationRequest certificationRequest) {
        CertificationDTO updatedCert = certificationService.updateCertification(certId, certificationRequest);
        return ResponseEntity.ok(updatedCert);
    }
}
