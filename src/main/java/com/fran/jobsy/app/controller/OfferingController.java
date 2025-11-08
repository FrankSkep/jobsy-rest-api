package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.common.UriBuilder;
import com.fran.jobsy.app.dto.offering.OfferingFilterModel;
import com.fran.jobsy.app.dto.offering.OfferingMinimalResponse;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import com.fran.jobsy.app.dto.offering.OfferingResponse;
import com.fran.jobsy.app.service.offering.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/offerings")
@RequiredArgsConstructor
@Tag(name = "Offerings", description = "Operaciones sobre los servicios ofrecidos por los proveedores")
public class OfferingController {

    private final OfferingService offeringService;

    @GetMapping
    @Operation(summary = "Listar servicios", description = "Devuelve una lista paginada de servicios, con posibilidad de filtrar por categoría, precio, calificación y ubicación.")
    public ResponseEntity<Page<OfferingMinimalResponse>> getOfferings(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        boolean hasFilters = categoryId != null || minPrice != null || maxPrice != null ||
                minRating != null || location != null;

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (hasFilters) {
            OfferingFilterModel filters = new OfferingFilterModel(
                    categoryId, minPrice, maxPrice, minRating, null, null, null, location
            );

            Page<OfferingMinimalResponse> result = offeringService.getOfferingsWithFiltersPaged(filters, pageable);
            return ResponseEntity.ok(result);
        }

        Page<OfferingMinimalResponse> result = offeringService.getOfferingsPage(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID", description = "Devuelve los detalles de un servicio específico por su ID.")
    public ResponseEntity<OfferingResponse> getOfferingById(@PathVariable Long id) {
        OfferingResponse offering = offeringService.getOffering(id);
        return ResponseEntity.ok(offering);
    }

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Crear servicio", description = "Crea un nuevo servicio ofrecido por un proveedor.")
    public ResponseEntity<OfferingResponse> createOffering(@RequestBody @Valid OfferingRequest offeringRequest) {
        OfferingResponse offering = offeringService.createOffering(offeringRequest);
        URI location = UriBuilder.buildCreatedLocation(offering.id());
        return ResponseEntity.created(location).body(offering);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Actualizar servicio", description = "Actualiza los detalles de un servicio ofrecido por un proveedor.")
    public ResponseEntity<OfferingResponse> updateOffering(
            @PathVariable Long id,
            @RequestBody @Valid OfferingRequest offeringRequest) {
        OfferingResponse updatedOffering = offeringService.updateOffering(id, offeringRequest);
        return ResponseEntity.ok(updatedOffering);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Eliminar servicio", description = "Elimina un servicio ofrecido por un proveedor.")
    public ResponseEntity<Void> deleteOffering(@PathVariable Long id) {
        offeringService.deleteOffering(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Activar/Desactivar servicio", description = "Cambia el estado activo/inactivo de un servicio.")
    public ResponseEntity<OfferingResponse> toggleOfferingStatus(@PathVariable Long id) {
        OfferingResponse offering = offeringService.toggleOfferingStatus(id);
        return ResponseEntity.ok(offering);
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Obtener servicios de usuario", description = "Devuelve la lista de servicios ofrecidos por un usuario público.")
    public ResponseEntity<List<OfferingMinimalResponse>> getUserOfferings(@PathVariable Long id) {
        return ResponseEntity.ok(offeringService.getUserOfferings(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener mis servicios", description = "Devuelve la lista de servicios ofrecidos por el proveedor autenticado.")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<List<OfferingMinimalResponse>> getMyOfferings() {
        return ResponseEntity.ok(offeringService.getMyOfferings());
    }
}