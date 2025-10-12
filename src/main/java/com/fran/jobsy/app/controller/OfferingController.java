package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingFilterDTO;
import com.fran.jobsy.app.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class OfferingController {

    private final OfferingService offeringService;

    @GetMapping
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

            Page<OfferingDTO> result = offeringService.getServicesWithFiltersPaged(filters, pageable);
            return ResponseEntity.ok(result);
        }

        Page<OfferingDTO> result = offeringService.getServicesPaged(pageable);
        return ResponseEntity.ok(result);
    }
}