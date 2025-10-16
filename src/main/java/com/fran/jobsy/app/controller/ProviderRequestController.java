package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.provider_request.ProviderProfileRequest;
import com.fran.jobsy.app.dto.provider_request.ProviderRejectionRequest;
import com.fran.jobsy.app.dto.provider_request.ProviderRequestDTO;
import com.fran.jobsy.app.exception.custom.InvalidFileException;
import com.fran.jobsy.app.service.ProviderRequestService;
import com.fran.jobsy.app.util.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Provider Requests", description = "Operaciones de postulación y gestión de proveedores")
public class ProviderRequestController {

    private final ProviderRequestService providerRequestService;
    private final FileValidator fileValidator;

    @PostMapping
    @Operation(summary = "Solicitar ser proveedor", description = "Permite a un usuario postularse como proveedor adjuntando documentos requeridos. Recibe en formData un objeto JSON con los datos del perfil y hasta 5 archivos (3 fotos propias y 2 INE (AMBOS LADOS)).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Solicitud enviada correctamente"),
        @ApiResponse(responseCode = "400", description = "Archivos inválidos o solicitud incorrecta")
    })
    public ResponseEntity<Void> applyForProvider(@RequestPart("providerProfile") @Valid ProviderProfileRequest providerProfileRequest,
                                                 @RequestPart("documents") List<MultipartFile> documents) {

        if (documents == null || documents.isEmpty()) {
            throw new InvalidFileException("Debe enviar los archivos requeridos.");
        }

        if (documents.size() > 5) {
            throw new InvalidFileException("No puede enviar más de 5 archivos. (3 fotos propias y 2 INE (AMBOS LADOS)");
        }

        documents.forEach(fileValidator::validate);

        providerRequestService.applyForProvider(providerProfileRequest, documents);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todas las solicitudes de proveedor", description = "Solo accesible para ADMIN. Devuelve todas las solicitudes de proveedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo ADMIN")
    })
    public ResponseEntity<Page<ProviderRequestDTO>> getAllProviderRequests(Pageable pageable) {
        Page<ProviderRequestDTO> requests = providerRequestService.getAllProviderRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aprobar solicitud de proveedor", description = "Solo accesible para ADMIN. Aprueba la solicitud de proveedor indicada.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Solicitud aprobada correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo ADMIN")
    })
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        providerRequestService.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechazar solicitud de proveedor", description = "Solo accesible para ADMIN. Rechaza la solicitud de proveedor indicada.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Solicitud rechazada correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo ADMIN")
    })
    public ResponseEntity<Void> reject(@PathVariable Long id,
                                       @Valid @RequestBody ProviderRejectionRequest body) {
        providerRequestService.reject(id, body.reason());
        return ResponseEntity.noContent().build();
    }
}
