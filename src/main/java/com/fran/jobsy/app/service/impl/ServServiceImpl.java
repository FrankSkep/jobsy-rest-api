package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.service.ServiceDTO;
import com.fran.jobsy.app.dto.service.ServiceFilterDTO;
import com.fran.jobsy.app.repository.ServiceRepository;
import com.fran.jobsy.app.service.ServService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServServiceImpl implements ServService {
    private final ServiceRepository serviceRepository;

    @Override
    public List<ServiceDTO> getServices() {
        return serviceRepository.findAllServices();
    }

    @Override
    public Page<ServiceDTO> getServicesPaged(Pageable pageable) {
        return serviceRepository.findAllServicesPaged(pageable);
    }

    @Override
    public List<ServiceDTO> getServicesWithFilters(ServiceFilterDTO filters) {
        return serviceRepository.findServicesWithFilters(
                filters.categoryId(),
                filters.minPrice(),
                filters.maxPrice(),
                filters.minRating(),
                filters.location()
        );
    }

    @Override
    public Page<ServiceDTO> getServicesWithFiltersPaged(ServiceFilterDTO filters, Pageable pageable) {
        return serviceRepository.findServicesWithFiltersPaged(
                filters.categoryId(),
                filters.minPrice(),
                filters.maxPrice(),
                filters.minRating(),
                filters.location(),
                pageable
        );
    }

    @Override
    public List<ServiceDTO> getServicesByUserId(Long userId) {
        return serviceRepository.findByOwnerId(userId);
    }
}