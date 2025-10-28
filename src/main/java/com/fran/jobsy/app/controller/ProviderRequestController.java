package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.providerrequest.ProviderApplyRequest;
import com.fran.jobsy.app.dto.providerrequest.ProviderRejectionRequest;
import com.fran.jobsy.app.dto.providerrequest.ProviderRequestResponse;
import com.fran.jobsy.app.exception.custom.InvalidFileException;
import com.fran.jobsy.app.service.providerrequest.ProviderRequestService;
import com.fran.jobsy.app.util.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/provider-requests")
@RequiredArgsConstructor
@Tag(name = "ProviderRequests", description = "Operaciones de postulación y gestión de proveedores")
public class ProviderRequestController {

    private final ProviderRequestService providerRequestService;
    private final FileValidator fileValidator;

    @PostMapping
    @Operation(summary = "Solicitar ser proveedor", description = "Permite a un usuario postularse como proveedor adjuntando documentos requeridos. Recibe en formData un objeto JSON con los datos del perfil y hasta 5 archivos (3 fotos propias y 2 INE (AMBOS LADOS)).")
    public ResponseEntity<Void> applyForProvider(@RequestPart("providerProfile") @Valid ProviderApplyRequest providerApplyRequest,
                                                 @RequestPart("documents") List<MultipartFile> documents) {

        if (documents == null || documents.isEmpty()) {
            throw new InvalidFileException("Debe enviar los archivos requeridos.");
        }

        if (documents.size() > 5) {
            throw new InvalidFileException("No puede enviar más de 5 archivos. (3 fotos propias y 2 INE (AMBOS LADOS)");
        }

        documents.forEach(fileValidator::validate);

        providerRequestService.applyForProvider(providerApplyRequest, documents);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Obtener todas las solicitudes de proveedor", description = "Solo accesible para ADMIN. Devuelve todas las solicitudes de proveedor.")
    public ResponseEntity<Page<ProviderRequestResponse>> getAllProviderRequests(Pageable pageable) {
        Page<ProviderRequestResponse> requests = providerRequestService.getAllProviderRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Aprobar solicitud de proveedor", description = "Solo accesible para ADMIN. Aprueba la solicitud de proveedor indicada.")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        providerRequestService.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Rechazar solicitud de proveedor", description = "Solo accesible para ADMIN. Rechaza la solicitud de proveedor indicada.")
    public ResponseEntity<Void> reject(@PathVariable Long id,
                                       @Valid @RequestBody ProviderRejectionRequest body) {
        providerRequestService.reject(id, body.reason());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @Operation(summary = "Obtener mi solicitud de proveedor", description = "Devuelve la solicitud de proveedor del usuario autenticado.")
    public ResponseEntity<ProviderRequestResponse> getMyProviderRequest() {
        ProviderRequestResponse request = providerRequestService.getMyProviderRequest();
        return ResponseEntity.ok(request);
    }
}
