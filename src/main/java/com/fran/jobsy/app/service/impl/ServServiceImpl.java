package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.service.ServiceDTO;
import com.fran.jobsy.app.dto.service.ServiceFilterDTO;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.entity.UserPhoto;
import com.fran.jobsy.app.repository.ServiceRepository;
import com.fran.jobsy.app.service.ServService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                filters.getCategoryId(),
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getMinRating(),
                filters.getLocation()
        );
    }

    @Override
    public Page<ServiceDTO> getServicesWithFiltersPaged(ServiceFilterDTO filters, Pageable pageable) {
        return serviceRepository.findServicesWithFiltersPaged(
                filters.getCategoryId(),
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getMinRating(),
                filters.getLocation(),
                pageable
        );
    }

    @Override
    public List<ServiceDTO> getServicesByUserId(Long userId) {
        return serviceRepository.findByOwnerId(userId);
    }

    // services profile images
//    public List<UserPhoto> updateProfileImage(List<MultipartFile> files) {
//        User user = authenticatedUserProvider.getAuthenticatedUser();
//
//        List<UserPhoto> uploadedImages = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            try {
//                Map uploadResult = cloudinaryService.upload(file);
//                String imageUrl = (String) uploadResult.get("url");
//                String imageId = (String) uploadResult.get("public_id");
//
//                UserPhoto userPhoto = UserPhoto.builder().imageId(imageId).url(imageUrl).user(entityManager.getReference(User.class, user.getId())).build();
//                uploadedImages.add(UserPhotoRepository.save(userPhoto));
//
//            } catch (
//                    Exception e) {
//                throw new RuntimeException("Image upload failed.", e);
//            }
//        }
//        return uploadedImages;
//    }
}