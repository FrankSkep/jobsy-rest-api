package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.auth.PasswordRequest;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.AuthenticationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.repository.UserWorkPhotoRepository;
import com.fran.jobsy.app.service.UserService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserWorkPhotoRepository userWorkPhotoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;

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
    public void updateUser(UserInfoUpdateRequest userReq) {
        User user = getById(authenticatedUserProvider.getAuthenticatedUserId());
        user.setFirstname(userReq.firstname());
        user.setLastname(userReq.lastname());
        user.setCountry(userReq.country());
        userRepository.save(user);
    }

    @Override
    public void updateUser(ProviderInfoUpdateRequest userReq) {
        User user = getById(authenticatedUserProvider.getAuthenticatedUserId());

        user.setBio(userReq.bio());
        user.setHourlyRate(userReq.hourlyRate());
        user.setYearsExperience(userReq.yearsExperience());
        user.setAddressText(userReq.addressText());
        user.setLat(userReq.lat());
        user.setLng(userReq.lng());
        user.setServiceRadiusKm(userReq.serviceRadiusKm());

        userRepository.save(user);
    }

    @Override
    public void updateRole(Long userId, Role role) {
        User user = getById(userId);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();
        User user = getById(id);

        if (user.getId().equals(authenticatedUserId)) {
            throw new AuthenticationException("No puedes eliminar tu propia cuenta.");
        }

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

        UserPhotoDTO photoDto = null;
        if (user.getPhoto() != null) {
            photoDto = new UserPhotoDTO(
                    user.getPhoto().getId(),
                    user.getPhoto().getImageId(),
                    user.getPhoto().getUrl()
            );
        }

        return new UserFullDTO(
                user.getId(),
                user.getUsername(),
                user.getLastname(),
                user.getFirstname(),
                photoDto,
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

    public void deleteMyAccount() {
        userRepository.deleteById(authenticatedUserProvider.getAuthenticatedUserId());
    }
}
