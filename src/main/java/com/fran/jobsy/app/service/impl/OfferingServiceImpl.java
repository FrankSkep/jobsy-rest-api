package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingFilterDTO;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import com.fran.jobsy.app.entity.Category;
import com.fran.jobsy.app.entity.Offering;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.exception.custom.UnauthorizedAccessException;
import com.fran.jobsy.app.mapper.OfferingMapper;
import com.fran.jobsy.app.repository.CategoryRepository;
import com.fran.jobsy.app.repository.OfferingRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.OfferingService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
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
    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final OfferingMapper offeringMapper;

    @Override
    public Page<OfferingDTO> getOfferingsWithFiltersPaged(OfferingFilterDTO filters, Pageable pageable) {
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
    public Page<OfferingDTO> getOfferingsPage(Pageable pageable) {
        return offeringRepository.findAllServicesPaged(pageable);
    }

    @Override
    public List<OfferingDTO> getOfferingsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (user.getRole() != Role.PROVIDER) {
            throw new UnauthorizedAccessException("El usuario con ID: " + userId + " no es un proveedor");
        }
        return offeringRepository.findByOwnerId(userId);
    }

    @Override
    public OfferingDTO createOffering(OfferingRequest offeringRequest) {
        User owner = authenticatedUserProvider.getAuthenticatedUser();

        if (owner.getRole() != Role.PROVIDER) {
            throw new UnauthorizedAccessException("El usuario autenticado no es un proveedor");
        }

        Category category = categoryRepository.findById(offeringRequest.category().id())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no existente: " + offeringRequest.category().name()));

        Offering offering = Offering.builder()
                .owner(authenticatedUserProvider.getUserReference(owner.getId()))
                .category(category)
                .title(offeringRequest.title())
                .description(offeringRequest.description())
                .basePrice(offeringRequest.basePrice())
                .build();

        return offeringMapper.toDTO(offeringRepository.save(offering));
    }

    @Override
    public OfferingDTO updateOffering(Long offeringId, OfferingRequest offeringRequest) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + offeringId));

        Long ownerId = authenticatedUserProvider.getAuthenticatedUserId();

        if (!offering.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("El usuario autenticado no es el propietario del servicio");
        }

        Category category = categoryRepository.findById(offeringRequest.category().id())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no existente: " + offeringRequest.category().name()));

        offering.setCategory(category);
        offering.setTitle(offeringRequest.title());
        offering.setDescription(offeringRequest.description());
        offering.setBasePrice(offeringRequest.basePrice());

        return offeringMapper.toDTO(offeringRepository.save(offering));
    }

    @Override
    public OfferingDTO getOffering(Long offeringId) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + offeringId));

        return offeringMapper.toDTO(offering);
    }
}