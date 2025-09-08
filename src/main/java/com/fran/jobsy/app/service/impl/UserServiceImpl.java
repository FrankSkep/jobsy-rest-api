package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.ProviderProfileRequest;
import com.fran.jobsy.app.dto.auth.PasswordRequest;
import com.fran.jobsy.app.dto.user.UserDTO;
import com.fran.jobsy.app.dto.user.UserRequest;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.auth.IncorrectPasswordException;
import com.fran.jobsy.app.exception.auth.UserNotFoundException;
import com.fran.jobsy.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements com.fran.jobsy.app.service.UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    @Override
    public void updateUser(String username, UserRequest user) {
        User userEntity = getByUsername(username);

        userEntity.setUsername(user.getUsername());
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

    @Override
    public UserDTO getUserInfoByUsername(String username) {
        User user = getByUsername(username);
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getLastname(),
                user.getFirstname(),
                user.getCountry(),
                user.getRole()
        );
    }

    public void createProviderProfile(ProviderProfileRequest providerProfileRequest, Long userId) {
        User userEntity = getById(userId);

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
}
