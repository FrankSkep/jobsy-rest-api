package com.fran.jobsy.auth.service;

import com.fran.jobsy.auth.dto.PasswordRequest;
import com.fran.jobsy.auth.dto.UserDTO;
import com.fran.jobsy.auth.dto.UserRequest;
import com.fran.jobsy.auth.entity.Role;
import com.fran.jobsy.auth.entity.User;
import com.fran.jobsy.auth.exception.IncorrectPasswordException;
import com.fran.jobsy.auth.exception.UserNotFoundException;
import com.fran.jobsy.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

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

    public void updateUser(String username, UserRequest user) {
        User userEntity = getByUsername(username);

        userEntity.setUsername(user.getUsername());
        userEntity.setFirstname(user.getFirstname());
        userEntity.setLastname(user.getLastname());
        userEntity.setCountry(user.getCountry());
        userRepository.save(userEntity);
    }

    public void updateRole(Long userId, Role role) {
        User user = getById(userId);
        user.setRole(role);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    public void deleteUser(String username) {
        User user = getByUsername(username);
        userRepository.delete(user);
    }

    public void updatePassword(String username, PasswordRequest password) {
        User user = getByUsername(username);

        if (passwordEncoder.matches(password.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password.getNewPassword()));
        } else {
            throw new IncorrectPasswordException("Old password does not match.");
        }
        userRepository.save(user);
    }

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
}
