package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.CertificationRequest;
import com.fran.jobsy.app.dto.CertificationResponse;
import com.fran.jobsy.app.service.certification.CertificationService;
import com.fran.jobsy.app.common.UriBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Certifications", description = "Operaciones sobre certificaciones de proveedores")
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/{id}/certifications")
    @Operation(summary = "Obtener certificaciones de usuario", description = "Devuelve la lista de certificaciones públicas de un usuario.")
    public ResponseEntity<List<CertificationResponse>> getMyCertifications(@PathVariable Long id) {
        List<CertificationResponse> certifications = certificationService.getUserCertifications(id);
        return ResponseEntity.ok(certifications);
    }

    @PostMapping("/me/certifications")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Agregar certificación", description = "Permite a un usuario con rol PROVIDER agregar una certificación. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<CertificationResponse> addCertification(@RequestBody @Valid CertificationRequest certificationRequest) {
        CertificationResponse certification = certificationService.addCertification(certificationRequest);
        URI location = UriBuilder.buildCreatedLocation(certification.id());
        return ResponseEntity.created(location).body(certification);
    }

    @DeleteMapping("/me/certifications/{certId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Eliminar certificación", description = "Permite a un usuario con rol PROVIDER eliminar una certificación. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certId) {
        certificationService.deleteCertification(certId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/certifications/{certId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Actualizar certificación", description = "Permite a un usuario con rol PROVIDER actualizar una certificación. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<CertificationResponse> updateCertification(@PathVariable Long certId, @RequestBody @Valid CertificationRequest certificationRequest) {
        CertificationResponse updatedCert = certificationService.updateCertification(certId, certificationRequest);
        return ResponseEntity.ok(updatedCert);
    }
}
