package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.user.UserPhotoDTO;
import com.fran.jobsy.app.entity.UserPhoto;
import com.fran.jobsy.app.exception.custom.CloudinaryException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserPhotoRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.UserPhotoService;
import com.fran.jobsy.app.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPhotoServiceImpl implements UserPhotoService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CloudinaryService cloudinaryService;
    private final UserPhotoRepository userPhotoRepository;

    @Override
    @Transactional
    public UserPhotoDTO updateUserPhoto(MultipartFile file) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        String newImageId = null;
        String oldImageId = null;

        try {
            // Save reference to old photo (if exists)
            UserPhoto oldPhoto = userPhotoRepository.findByUserId(userId).orElse(null);
            if (oldPhoto != null) {
                oldImageId = oldPhoto.getImageId();
            }

            // Upload new photo
            Map uploadResult = cloudinaryService.upload(file);
            String imageUrl = (String) uploadResult.get("url");
            newImageId = (String) uploadResult.get("public_id");

            // Save to database
            UserPhoto userPhoto;
            if (oldPhoto != null) {
                // Update existing photo
                oldPhoto.setImageId(newImageId);
                oldPhoto.setUrl(imageUrl);
                userPhoto = userPhotoRepository.save(oldPhoto);
            } else {
                // Create new photo
                userPhoto = UserPhoto.builder()
                        .imageId(newImageId)
                        .url(imageUrl)
                        .user(authenticatedUserProvider.getUserReference(userId))
                        .build();
                userPhoto = userPhotoRepository.save(userPhoto);
            }

            // Delete old photo from Cloudinary AFTER successful save
            if (oldImageId != null && !oldImageId.equals(newImageId)) {
                try {
                    cloudinaryService.delete(oldImageId);
                } catch (
                        Exception e) {
                    throw new CloudinaryException("Error al eliminar la foto antigua: " + e.getMessage());
                }
            }

            return new UserPhotoDTO(userPhoto.getId(), userPhoto.getImageId(), userPhoto.getUrl());

        } catch (
                Exception e) {
            // Rollback: Delete newly uploaded image if database save fails
            if (newImageId != null) {
                try {
                    cloudinaryService.delete(newImageId);
                } catch (
                        Exception ex) {
                    throw new CloudinaryException("Error al eliminar la nueva foto durante el rollback: " + ex.getMessage());
                }
            }
            throw new CloudinaryException("Error al actualizar la foto de perfil: " + e.getMessage());
        }
    }

    @Override
    public UserPhotoDTO getUserPhoto(Long id) {
        UserPhoto photo = userPhotoRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la foto de perfil para el usuario con id: " + id));
        return new UserPhotoDTO(photo.getId(), photo.getImageId(), photo.getUrl());
    }

    @Override
    @Transactional
    public void deleteUserPhoto() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        UserPhoto userPhoto = userPhotoRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No hay foto de perfil para eliminar."));

        String imageId = userPhoto.getImageId();
        userPhotoRepository.deleteByUserId(userId);

        try {
            cloudinaryService.delete(imageId);
        } catch (
                IOException e) {
            throw new CloudinaryException("Error al eliminar la foto de perfil: " + e.getMessage());
        }
    }
}