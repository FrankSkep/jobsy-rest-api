package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.entity.UserPhoto;
import com.fran.jobsy.app.exception.custom.CloudinaryException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserPhotoRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.UserPhotoService;
import com.fran.jobsy.app.utils.AuthenticatedUserProvider;
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
    public UserPhoto updateUserPhoto(MultipartFile file) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        try {
            Map uploadResult = cloudinaryService.upload(file);
            String imageUrl = (String) uploadResult.get("url");
            String imageId = (String) uploadResult.get("public_id");

            UserPhoto userPhoto = UserPhoto.builder().imageId(imageId).url(imageUrl).user(user).build();
            return UserPhotoRepository.save(userPhoto);
        } catch (
                Exception e) {
            throw new RuntimeException("Image upload failed.", e);
        }
    }

    @Override
    public void deleteUserPhoto() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        UserPhoto userPhoto = UserPhotoRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No profile image to delete."));

        try {
            cloudinaryService.delete(userPhoto.getImageId());
            UserPhotoRepository.delete(userPhoto);
        } catch (
                Exception e) {
            throw new CloudinaryException("Failed to delete profile image." + e.getMessage());
        }
    }
}
