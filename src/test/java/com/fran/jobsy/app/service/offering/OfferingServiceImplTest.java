package com.fran.jobsy.app.service.offering;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.offering.OfferingRequest;
import com.fran.jobsy.app.dto.offering.OfferingResponse;
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
import com.fran.jobsy.app.service.imagestorage.ImageStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferingServiceImplTest {

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private OfferingMapper offeringMapper;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private OfferingServiceImpl offeringService;

    @Test
    void testCreateOffering_Success() {
        // Arrange
        Long userId = 1L;
        Long categoryId = 10L;
        Long offeringId = 100L;

        User mockProvider = User.builder()
                .id(userId)
                .username("provider@test.com")
                .firstname("John")
                .lastname("Doe")
                .role(Role.PROVIDER)
                .lat(40.7128)
                .lng(-74.0060)
                .build();

        Category mockCategory = Category.builder()
                .id(categoryId)
                .name("Plomería")
                .build();

        OfferingRequest request = new OfferingRequest(
                categoryId,
                "Servicio de Plomería Profesional",
                "Instalación y reparación de tuberías y sistemas de agua",
                500.0,
                5.0
        );

        Offering mockOffering = Offering.builder()
                .id(offeringId)
                .owner(mockProvider)
                .category(mockCategory)
                .title(request.title())
                .description(request.description())
                .basePrice(request.basePrice())
                .yearsOfExperience(request.yearsOfExperience())
                .isActive(true)
                .build();

        OfferingResponse expectedResponse = new OfferingResponse(
                offeringId,
                null,
                mockCategory.getName(),
                request.title(),
                request.description(),
                request.basePrice(),
                request.yearsOfExperience(),
                true,
                null,
                null,
                null
        );

        // Mock behavior
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockProvider);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(offeringRepository.save(any(Offering.class))).thenReturn(mockOffering);
        when(offeringMapper.toDTO(any(Offering.class))).thenReturn(expectedResponse);

        // Act
        OfferingResponse result = offeringService.createOffering(request);

        // Assert
        assertNotNull(result);
        assertEquals(offeringId, result.id());
        assertEquals(mockCategory.getName(), result.category());
        assertEquals(request.title(), result.title());
        assertEquals(request.description(), result.description());
        assertEquals(request.basePrice(), result.basePrice());
        assertEquals(request.yearsOfExperience(), result.yearsOfExperience());
        assertTrue(result.isActive());

        // Verify interactions
        verify(authenticatedUserProvider, times(1)).getAuthenticatedUser();
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(offeringRepository, times(1)).save(any(Offering.class));
        verify(offeringMapper, times(1)).toDTO(any(Offering.class));
    }

    @Test
    void testCreateOffering_UserNotProvider_ThrowsUnauthorizedAccessException() {
        // Arrange
        Long categoryId = 10L;
        User mockUser = User.builder()
                .id(1L)
                .username("user@test.com")
                .role(Role.USER)
                .build();

        OfferingRequest request = new OfferingRequest(
                categoryId,
                "Servicio de Plomería",
                "Descripción del servicio",
                500.0,
                5.0
        );

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> offeringService.createOffering(request)
        );

        assertEquals("El usuario autenticado no es un proveedor", exception.getMessage());
        verify(authenticatedUserProvider, times(1)).getAuthenticatedUser();
        verify(categoryRepository, never()).findById(anyLong());
        verify(offeringRepository, never()).save(any(Offering.class));
    }

    @Test
    void testCreateOffering_ProviderWithoutLocation_ThrowsUnauthorizedAccessException() {
        // Arrange
        Long categoryId = 10L;
        User mockProvider = User.builder()
                .id(1L)
                .username("provider@test.com")
                .role(Role.PROVIDER)
                .lat(null)
                .lng(null)
                .build();

        OfferingRequest request = new OfferingRequest(
                categoryId,
                "Servicio de Plomería",
                "Descripción del servicio",
                500.0,
                5.0
        );

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockProvider);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> offeringService.createOffering(request)
        );

        assertEquals("El proveedor debe tener una ubicación establecida para crear un servicio", exception.getMessage());
        verify(authenticatedUserProvider, times(1)).getAuthenticatedUser();
        verify(categoryRepository, never()).findById(anyLong());
        verify(offeringRepository, never()).save(any(Offering.class));
    }

    @Test
    void testCreateOffering_CategoryNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long categoryId = 999L;
        User mockProvider = User.builder()
                .id(1L)
                .username("provider@test.com")
                .role(Role.PROVIDER)
                .lat(40.7128)
                .lng(-74.0060)
                .build();

        OfferingRequest request = new OfferingRequest(
                categoryId,
                "Servicio de Plomería",
                "Descripción del servicio",
                500.0,
                5.0
        );

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockProvider);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> offeringService.createOffering(request)
        );

        assertEquals("Categoría no existente con ID: " + categoryId, exception.getMessage());
        verify(authenticatedUserProvider, times(1)).getAuthenticatedUser();
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(offeringRepository, never()).save(any(Offering.class));
    }
}

