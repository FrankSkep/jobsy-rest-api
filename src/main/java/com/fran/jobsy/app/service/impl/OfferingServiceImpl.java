package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingFilterDTO;
import com.fran.jobsy.app.repository.OfferingRepository;
import com.fran.jobsy.app.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferingServiceImpl implements OfferingService {
    private final OfferingRepository offeringRepository;

    @Override
    public List<OfferingDTO> getServices() {
        return offeringRepository.findAllServices();
    }

    @Override
    public Page<OfferingDTO> getServicesPaged(Pageable pageable) {
        return offeringRepository.findAllServicesPaged(pageable);
    }

    @Override
    public List<OfferingDTO> getServicesWithFilters(OfferingFilterDTO filters) {
        return offeringRepository.findServicesWithFilters(
                filters.categoryId(),
                filters.minPrice(),
                filters.maxPrice(),
                filters.minRating(),
                filters.location()
        );
    }

    @Override
    public Page<OfferingDTO> getServicesWithFiltersPaged(OfferingFilterDTO filters, Pageable pageable) {
        return offeringRepository.findServicesWithFiltersPaged(
                filters.categoryId(),
                filters.minPrice(),
                filters.maxPrice(),
                filters.minRating(),
                filters.location(),
                pageable
        );
    }

    @Override
    public List<OfferingDTO> getServicesByUserId(Long userId) {
        return offeringRepository.findByOwnerId(userId);
    }
}