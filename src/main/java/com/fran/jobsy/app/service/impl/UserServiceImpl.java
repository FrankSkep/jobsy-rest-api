package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.auth.PasswordRequest;
import com.fran.jobsy.app.dto.service.ServiceDTO;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.entity.UserPhoto;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.auth.IncorrectPasswordException;
import com.fran.jobsy.app.exception.auth.UserNotFoundException;
import com.fran.jobsy.app.repository.UserPhotoRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.ServService;
import com.fran.jobsy.app.service.UserService;
import com.fran.jobsy.app.utils.AuthenticatedUserProvider;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPhotoRepository UserPhotoRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ServService servService;
    private final EntityManager entityManager;

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllAsUserDTO();
    }

    @Override
    public void updateUser(UserRequest user) {
        User userEntity = authenticatedUserProvider.getAuthenticatedUser();

        userEntity.setFirstname(user.getFirstname());
        userEntity.setLastname(user.getLastname());
        userEntity.setCountry(user.getCountry());
        userRepository.save(userEntity);
    }


    @Override
    public void updateRole(Long userId, Role role) {
        User user = getById(userId);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    @Override
    public void deleteUser(String username) {
        User user = getByUsername(username);
        userRepository.delete(user);
    }

    @Override
    public void updatePassword(String username, PasswordRequest password) {
        User user = getByUsername(username);

        if (passwordEncoder.matches(password.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password.getNewPassword()));
        } else {
            throw new IncorrectPasswordException("Old password does not match.");
        }
        userRepository.save(user);
    }

    public UserPublicDTO getUser(Long id) {
        User user = getById(id);

        return new UserPublicDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getCountry(),
                user.getBio(),
                user.getHourlyRate(),
                user.getYearsExperience(),
                user.getAddressText(),
                user.getServiceRadiusKm()
        );
    }

    // === Authenticated User Methods ===
    @Override
    public UserFullDTO getUserInfo() {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        return new UserFullDTO(
                user.getId(),
                user.getUsername(),
                user.getLastname(),
                user.getFirstname(),
                user.getCountry(),
                user.getRole(),
                user.getBio(),
                user.getHourlyRate(),
                user.getYearsExperience(),
                user.getAddressText(),
                user.getLat(),
                user.getLng(),
                user.getServiceRadiusKm(),
                user.getVerifiedCert()
        );
    }

    public void updateProviderInfo(ProviderProfileRequest providerProfileRequest) {
        User userEntity = authenticatedUserProvider.getAuthenticatedUser();

        userEntity.setBio(providerProfileRequest.getBio());
        userEntity.setHourlyRate(providerProfileRequest.getHourlyRate());
        userEntity.setYearsExperience(providerProfileRequest.getYearsExperience());
        userEntity.setAddressText(providerProfileRequest.getAddressText());
        userEntity.setLat(providerProfileRequest.getLat());
        userEntity.setLng(providerProfileRequest.getLng());
        userEntity.setServiceRadiusKm(providerProfileRequest.getServiceRadiusKm());
        userEntity.setVerifiedCert(providerProfileRequest.getVerifiedCert());
        userRepository.save(userEntity);
    }

    @Override
    public List<ServiceDTO> getUserServices(Long userId) {
        getById(userId);
        return servService.getServicesByUserId(userId);
    }

    public void deleteMyAccount() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        userRepository.delete(user);
    }

    public UserPhoto updateProfileImage(MultipartFile file) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        try {
            Map uploadResult = cloudinaryService.upload(file);
            String imageUrl = (String) uploadResult.get("url");
            String imageId = (String) uploadResult.get("public_id");

            UserPhoto userPhoto = UserPhoto.builder().imageId(imageId).url(imageUrl).user(entityManager.getReference(User.class, user.getId())).build();
            return UserPhotoRepository.save(userPhoto);
        } catch (
                Exception e) {
            throw new RuntimeException("Image upload failed.", e);
        }
    }

    public void deleteMyProfileImage() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        UserPhoto userPhoto = UserPhotoRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No profile image to delete."));

        try {
            cloudinaryService.delete(userPhoto.getImageId());
            UserPhotoRepository.delete(userPhoto);
        } catch (
                Exception e) {
            throw new RuntimeException("Failed to delete profile image.", e);
        }
    }
}
