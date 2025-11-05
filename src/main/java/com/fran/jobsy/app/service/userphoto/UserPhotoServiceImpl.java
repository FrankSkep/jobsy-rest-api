package com.fran.jobsy.app.service.userphoto;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.user.UserPhotoResponse;
import com.fran.jobsy.app.entity.UserPhoto;
import com.fran.jobsy.app.exception.custom.FileOperationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.mapper.UserPhotoMapper;
import com.fran.jobsy.app.repository.UserPhotoRepository;
import com.fran.jobsy.app.service.imagestorage.ImageStorageService;
import com.fran.jobsy.app.service.imagestorage.ImageUploadResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPhotoServiceImpl implements UserPhotoService {

    @Getter
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ImageStorageService imageStorageService;
    private final UserPhotoRepository userPhotoRepository;
    private final UserPhotoMapper userPhotoMapper;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersFull", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "usersPublic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "userPhotos", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()")
    })
    public UserPhotoResponse updateUserPhoto(MultipartFile file) {
        final Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        String newImageId = null;
        String oldImageId = null;

        try {
            // Load existing photo (if any)
            Optional<UserPhoto> existingPhotoOpt = userPhotoRepository.findByUserId(userId);
            if (existingPhotoOpt.isPresent()) {
                oldImageId = existingPhotoOpt.get().getImageId();
            }

            // Upload new image
            final ImageUploadResult uploadResult = imageStorageService.upload(file);
            final String imageUrl = uploadResult.url();
            final String publicId = uploadResult.publicId();
            newImageId = publicId; // keep for cleanup on failure

            // Upsert user photo
            UserPhoto toPersist = existingPhotoOpt.map(photo -> {
                photo.setImageId(publicId);
                photo.setUrl(imageUrl);
                return photo;
            }).orElseGet(() -> UserPhoto.builder()
                    .imageId(publicId)
                    .url(imageUrl)
                    .user(authenticatedUserProvider.getUserReference(userId))
                    .build());

            // Persist changes
            UserPhoto saved = userPhotoRepository.save(toPersist);

            // After successful save, delete old image if it changed
            if (oldImageId != null && !oldImageId.equals(publicId)) {
                final String oldImageIdFinal = oldImageId;
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        imageStorageService.deleteSafelyAsync(oldImageIdFinal);
                    }
                });
            }

            return userPhotoMapper.toDTO(saved);

        } catch (
                Exception e) {
            // On failure, clean up the newly uploaded image
            if (newImageId != null) {
                imageStorageService.deleteSafely(newImageId);
            }
            throw new FileOperationException("Error al actualizar la foto de perfil: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "userPhotos", key = "#id")
    public UserPhotoResponse getUserPhoto(Long id) {
        UserPhoto userPhoto = userPhotoRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la foto de perfil para el usuario con id: " + id));
        return userPhotoMapper.toDTO(userPhoto);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersFull", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "usersPublic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "userPhotos", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()")
    })
    public void deleteUserPhoto() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        UserPhoto userPhoto = userPhotoRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No hay foto de perfil para eliminar."));

        String imageId = userPhoto.getImageId();
        userPhotoRepository.deleteByUserId(userId);

        imageStorageService.deleteSafely(imageId);
    }
}
