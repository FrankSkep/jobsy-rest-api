package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingFilterDTO;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import com.fran.jobsy.app.service.OfferingService;
import com.fran.jobsy.app.util.RestUtils;
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

@RestController
@RequestMapping("/api/v1/offerings")
@RequiredArgsConstructor
@Tag(name = "Offerings", description = "Operaciones sobre los servicios ofrecidos por los proveedores")
public class OfferingController {

    private final OfferingService offeringService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Crear servicio", description = "Crea un nuevo servicio ofrecido por un proveedor.")
    public ResponseEntity<OfferingDTO> createOffering(@RequestBody @Valid OfferingRequest offeringRequest) {
        OfferingDTO offering = offeringService.createOffering(offeringRequest);
        URI location = RestUtils.buildCreatedLocation(offering.id());
        return ResponseEntity.created(location).body(offering);
    }

    @GetMapping
    @Operation(summary = "Listar servicios", description = "Devuelve una lista paginada de servicios, con posibilidad de filtrar por categoría, precio, calificación y ubicación.")
    public ResponseEntity<Page<OfferingDTO>> getOfferings(
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
            OfferingFilterDTO filters = new OfferingFilterDTO(
                    categoryId, minPrice, maxPrice, minRating, null, null, null, location
            );

            Page<OfferingDTO> result = offeringService.getOfferingsWithFiltersPaged(filters, pageable);
            return ResponseEntity.ok(result);
        }

        Page<OfferingDTO> result = offeringService.getOfferingsPage(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID", description = "Devuelve los detalles de un servicio específico por su ID.")
    public ResponseEntity<OfferingDTO> getOfferingById(@PathVariable Long id) {
        OfferingDTO offering = offeringService.getOffering(id);
        return ResponseEntity.ok(offering);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Actualizar servicio", description = "Actualiza los detalles de un servicio ofrecido por un proveedor.")
    public ResponseEntity<OfferingDTO> updateOffering(
            @PathVariable Long id,
            @RequestBody @Valid OfferingRequest offeringRequest) {
        OfferingDTO updatedOffering = offeringService.updateOffering(id, offeringRequest);
        return ResponseEntity.ok(updatedOffering);
    }

//    @GetMapping("/api/v1/users/{id}/services")
//    @Operation(summary = "Obtener servicios de usuario", description = "Devuelve la lista de servicios ofrecidos por un usuario público.")
//    public ResponseEntity<List<OfferingDTO>> getUserServices(@PathVariable Long id) {
//        return ResponseEntity.ok(offeringService.getServicesByUserId(id));
//    }
}