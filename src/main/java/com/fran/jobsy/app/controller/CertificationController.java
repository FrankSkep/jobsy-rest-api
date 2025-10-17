package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.CertificationDTO;
import com.fran.jobsy.app.dto.CertificationRequest;
import com.fran.jobsy.app.service.CertificationService;
import com.fran.jobsy.app.util.RestUtils;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Certifications", description = "Operaciones sobre certificaciones de proveedores")
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("users/{id}/certifications")
    @Operation(summary = "Obtener certificaciones de usuario", description = "Devuelve la lista de certificaciones públicas de un usuario.")
    public ResponseEntity<List<CertificationDTO>> getMyCertifications(@PathVariable Long id) {
        List<CertificationDTO> certifications = certificationService.getUserCertifications(id);
        return ResponseEntity.ok(certifications);
    }

    @PostMapping("/users/me/certifications")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Agregar certificación", description = "Permite a un usuario con rol PROVIDER agregar una certificación. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<CertificationDTO> addCertification(@RequestBody @Valid CertificationRequest certificationRequest) {
        CertificationDTO certification = certificationService.addCertification(certificationRequest);
        URI location = RestUtils.buildCreatedLocation(certification.id());
        return ResponseEntity.created(location).body(certification);
    }

    @DeleteMapping("/users/me/certifications/{certId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Eliminar certificación", description = "Permite a un usuario con rol PROVIDER eliminar una certificación. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certId) {
        certificationService.deleteCertification(certId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/me/certifications/{certId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Actualizar certificación", description = "Permite a un usuario con rol PROVIDER actualizar una certificación. Requiere autenticación y rol PROVIDER.")
    public ResponseEntity<CertificationDTO> updateCertification(@PathVariable Long certId, @RequestBody @Valid CertificationRequest certificationRequest) {
        CertificationDTO updatedCert = certificationService.updateCertification(certId, certificationRequest);
        return ResponseEntity.ok(updatedCert);
    }
}
