package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.user.UserWorkPhotoDTO;
import com.fran.jobsy.app.entity.UserWorkPhoto;
import com.fran.jobsy.app.exception.custom.CloudinaryException;
import com.fran.jobsy.app.exception.custom.FileOperationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserWorkPhotoRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.UserWorkPhotoService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserWorkPhotoServiceImpl implements UserWorkPhotoService {

    private final UserWorkPhotoRepository userWorkPhotoRepository;
    private final CloudinaryService cloudinaryService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional
    public void uploadWorkPhotos(List<MultipartFile> files) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        List<String> uploadedImageIds = new ArrayList<>(); // To track uploaded images for rollback

        try {
            for (MultipartFile file : files) {
                Map uploadResult = cloudinaryService.upload(file);
                String imageUrl = (String) uploadResult.get("url");
                String imageId = (String) uploadResult.get("public_id");
                uploadedImageIds.add(imageId);

                UserWorkPhoto workPhoto = UserWorkPhoto.builder()
                        .imageId(imageId)
                        .url(imageUrl)
                        .user(authenticatedUserProvider.getUserReference(userId))
                        .build();
                userWorkPhotoRepository.save(workPhoto);
            }
        } catch (
                Exception e) {
            // Rollback: delete any uploaded images in case of failure
            uploadedImageIds.forEach(imageId -> {
                try {
                    cloudinaryService.delete(imageId);
                } catch (
                        Exception ex) {
                    throw new CloudinaryException("Error al eliminar imagen durante el rollback: " + ex.getMessage());
                }
            });
            throw new FileOperationException("Error al subir imágenes: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeWorkPhoto(Long workPhotoId) {
        UserWorkPhoto workPhoto = userWorkPhotoRepository.findById(workPhotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto no encontrada"));

        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        if (!workPhoto.getUser().getId().equals(userId)) {
            throw new SecurityException("No tienes permiso para eliminar esta foto");
        }

        try {
            cloudinaryService.delete(workPhoto.getImageId());
            userWorkPhotoRepository.delete(workPhoto);
        } catch (
                Exception e) {
            throw new CloudinaryException("Error al eliminar foto: " + e.getMessage());
        }
    }

    @Override
    public List<UserWorkPhotoDTO> getUserWorkPhotos(Long userId) {
        return userWorkPhotoRepository.findWorkPhotosByUserId(userId);
    }
}
