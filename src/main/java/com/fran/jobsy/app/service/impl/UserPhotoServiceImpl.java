package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.user.UserPhotoDTO;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.entity.UserPhoto;
import com.fran.jobsy.app.exception.custom.CloudinaryException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserPhotoRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.UserPhotoService;
import com.fran.jobsy.app.utils.AuthenticatedUserProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPhotoServiceImpl implements UserPhotoService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CloudinaryService cloudinaryService;
    private final UserPhotoRepository UserPhotoRepository;

    @Override
    @Transactional
    public UserPhotoDTO updateUserPhoto(MultipartFile file) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        String newImageId = null;
        String oldImageId = null;

        try {
            // Save reference to old photo (if exists)
            UserPhoto oldPhoto = UserPhotoRepository.findByUserId(user.getId()).orElse(null);
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
                userPhoto = UserPhotoRepository.save(oldPhoto);
            } else {
                // Create new photo
                userPhoto = UserPhoto.builder()
                        .imageId(newImageId)
                        .url(imageUrl)
                        .user(user)
                        .build();
                userPhoto = UserPhotoRepository.save(userPhoto);
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
                    // Log rollback error
                }
            }
            throw new CloudinaryException("Error al actualizar la foto de perfil: " + e.getMessage());
        }
    }

    @Override
    public UserPhotoDTO getUserPhoto(Long id) {
        UserPhoto photo = UserPhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la foto de perfil con id: " + id));
        return new UserPhotoDTO(photo.getId(), photo.getImageId(), photo.getUrl());
    }

    @Override
    @Transactional
    public void deleteUserPhoto() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        UserPhoto userPhoto = UserPhotoRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No hay foto de perfil para eliminar."));

        try {
            cloudinaryService.delete(userPhoto.getImageId());
            UserPhotoRepository.delete(userPhoto);
        } catch (
                Exception e) {
            throw new CloudinaryException("Error al eliminar la foto de perfil: " + e.getMessage());
        }
    }
}