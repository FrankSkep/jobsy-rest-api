package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.auth.PasswordRequest;
import com.fran.jobsy.app.dto.service.ServiceDTO;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.AuthenticationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserPhotoRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.repository.UserWorkPhotoRepository;
import com.fran.jobsy.app.service.CloudinaryService;
import com.fran.jobsy.app.service.ServService;
import com.fran.jobsy.app.service.UserService;
import com.fran.jobsy.app.utils.AuthenticatedUserProvider;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPhotoRepository UserPhotoRepository;
    private final UserWorkPhotoRepository userWorkPhotoRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ServService servService;
    private final EntityManager entityManager;

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllAsUserDTO();
    }

    @Override
    public void updateUser(UserRequest user) {
        User userEntity = authenticatedUserProvider.getAuthenticatedUser();
        userEntity.setFirstname(user.firstname());
        userEntity.setLastname(user.lastname());
        userEntity.setCountry(user.country());
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
            throw new AuthenticationException("Old password does not match.");
        }
        userRepository.save(user);
    }

    @Override
    public UserPublicDTO getUser(Long id) {
        User user = getById(id);

        return new UserPublicDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getPhoto().getUrl(),
                userWorkPhotoRepository.findWorkPhotosByUserId(id),
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
                new UserPhotoDTO(user.getPhoto().getId(), user.getPhoto().getImageId(), user.getPhoto().getUrl()),
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

    @Override
    public void updateProviderInfo(ProviderProfileRequest providerProfileRequest) {
        User userEntity = authenticatedUserProvider.getAuthenticatedUser();

        userEntity.setBio(providerProfileRequest.bio());
        userEntity.setHourlyRate(providerProfileRequest.hourlyRate());
        userEntity.setYearsExperience(providerProfileRequest.yearsExperience());
        userEntity.setAddressText(providerProfileRequest.addressText());
        userEntity.setLat(providerProfileRequest.lat());
        userEntity.setLng(providerProfileRequest.lng());
        userEntity.setServiceRadiusKm(providerProfileRequest.serviceRadiusKm());
        userEntity.setVerifiedCert(providerProfileRequest.verifiedCert());
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
}
