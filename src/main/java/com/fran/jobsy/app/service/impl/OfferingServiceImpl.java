package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingFilterDTO;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.OfferingRepository;
import com.fran.jobsy.app.repository.UserRepository;
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
    private final UserRepository userRepository;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (user.getRole() != Role.PROVIDER) {
            throw new ConflictException("El usuario con ID: " + userId + " no es un proveedor");
        }
        return offeringRepository.findByOwnerId(userId);
    }
}