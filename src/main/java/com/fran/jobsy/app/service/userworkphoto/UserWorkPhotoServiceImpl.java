package com.fran.jobsy.app.service.userworkphoto;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.user.UserWorkPhotoResponse;
import com.fran.jobsy.app.entity.UserWorkPhoto;
import com.fran.jobsy.app.exception.custom.FileOperationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserWorkPhotoRepository;
import com.fran.jobsy.app.service.cloudinary.CloudinaryService;
import com.fran.jobsy.app.service.imagestorage.ImageStorageService;
import com.fran.jobsy.app.service.imagestorage.ImageUploadResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWorkPhotoServiceImpl implements UserWorkPhotoService {

    private final UserWorkPhotoRepository userWorkPhotoRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageStorageService imageStorageService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional
    public void uploadWorkPhotos(List<MultipartFile> files) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        List<ImageUploadResult> uploadResults = new ArrayList<>();

        try {
            uploadResults = imageStorageService.uploadAll(files);

            List<UserWorkPhoto> workPhotos = uploadResults.stream()
                    .map(result -> UserWorkPhoto.builder()
                            .imageId(result.publicId())
                            .url(result.url())
                            .user(authenticatedUserProvider.getUserReference(userId))
                            .build())
                    .toList();

            userWorkPhotoRepository.saveAll(workPhotos);

        } catch (
                Exception e) { // Rollback
            List<String> publicIds = uploadResults.stream()
                    .map(ImageUploadResult::publicId)
                    .toList();
            imageStorageService.deleteAllSafely(publicIds);

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

        imageStorageService.deleteSafely(workPhoto.getImageId());
        userWorkPhotoRepository.delete(workPhoto);
    }

    @Override
    public List<UserWorkPhotoResponse> getUserWorkPhotos(Long userId) {
        return userWorkPhotoRepository.findWorkPhotosByUserId(userId);
    }
}
