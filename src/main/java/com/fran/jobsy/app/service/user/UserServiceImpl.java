package com.fran.jobsy.app.service.user;

import com.fran.jobsy.app.annotation.EvictAuthenticatedUserCaches;
import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.auth.PasswordUpdateRequest;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.AuthenticationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.exception.custom.RoleAssignmentException;
import com.fran.jobsy.app.mapper.UserMapper;
import com.fran.jobsy.app.repository.ReviewRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.imagestorage.ImageStorageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ReviewRepository reviewRepository;
    @Getter
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ImageStorageService imageStorageService;

    // --- CRUD Operations ---
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllAsUserDTO();
    }

    @Override
    @Cacheable(value = "usersPublic", key = "#id")
    public UserPublicResponse getPublicInfo(String slug) {
        User user = userRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con Slug:" + slug));
        return userMapper.toPublic(user, reviewRepository);
    }

    @Override
    @Cacheable(value = "usersFull", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()")
    public UserFullResponse getMyFullInfo() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return userMapper.toFull(user);
    }

    @Override
    @Cacheable(value = "usersBasic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()")
    public UserResponse getMyBasicInfo() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return userMapper.toDTO(user);
    }

    @Override
    @EvictAuthenticatedUserCaches
    @Transactional
    public void updateUser(UserPatchRequest userReq) {
        User user = getById(authenticatedUserProvider.getAuthenticatedUserId());
        userMapper.updateFromDTO(userReq, user);
    }

    @Override
    @EvictAuthenticatedUserCaches
    @Transactional
    public void deleteMyAccount() {
        User user = getById(authenticatedUserProvider.getAuthenticatedUserId());
        deleteUserAssets(user);
        userRepository.delete(user);
    }

    @Override
    @EvictAuthenticatedUserCaches
    @Transactional
    public void updateUser(UserFullPatchRequest userReq) {
        User user = getById(authenticatedUserProvider.getAuthenticatedUserId());
        userMapper.updateFromFullDTO(userReq, user);
    }


    // --- Admin Operations ---
    @Override
    @Caching(evict = {
            @CacheEvict(value = "usersPublic", key = "#userId"),
            @CacheEvict(value = "usersBasic", key = "#userId"),
            @CacheEvict(value = "usersFull", key = "#userId")
    })
    public void updateRole(Long userId, Role newRole) {
        User target = getById(userId);
        User authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();

        // You cannot change your own role
        if (target.getId().equals(authenticatedUser.getId())) {
            throw new AuthenticationException("No puedes cambiar tu propio rol.");
        }

        // Already has the role
        if (target.getRole() == newRole) {
            throw new RoleAssignmentException("El usuario ya tiene este rol asignado.");
        }

        // Cannot change the role of another ADMIN unless you are SUPER_ADMIN
        if (target.getRole() == Role.ADMIN && authenticatedUser.getRole() != Role.SUPER_ADMIN) {
            throw new AuthenticationException("Solo un SUPER_ADMIN puede modificar el rol de un ADMIN.");
        }

        // Validate that the new role can be assigned by the authenticated user
        if (!canAssign(authenticatedUser.getRole(), newRole)) {
            throw new AuthenticationException("No tienes permisos para asignar este rol.");
        }

        // Only one SUPER_ADMIN can exist
        if (newRole == Role.SUPER_ADMIN && userRepository.existsByRole(Role.SUPER_ADMIN)) {
            throw new RoleAssignmentException("Ya existe un SUPER_ADMIN en el sistema.");
        }

        // Apply the change
        target.setRole(newRole);
        userRepository.save(target);
    }

    @Override
    public void updatePassword(PasswordUpdateRequest password) {
        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        User user = getById(authenticatedUserId);

        if (passwordEncoder.matches(password.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password.getNewPassword()));
        } else {
            throw new AuthenticationException("Contraseña antigua incorrecta.");
        }
        userRepository.save(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "usersPublic", key = "#id"),
            @CacheEvict(value = "usersBasic", key = "#id"),
            @CacheEvict(value = "usersFull", key = "#id")
    })
    public void deleteUser(Long id) {
        User authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();
        User target = getById(id);

        // You cannot delete yourself
        if (target.getId().equals(authenticatedUser.getId())) {
            throw new AuthenticationException("No puedes eliminar tu propia cuenta.");
        }

        // Only SUPER_ADMIN and ADMIN can delete users (in case the controller doesn't validate it)
        if (authenticatedUser.getRole() != Role.SUPER_ADMIN && authenticatedUser.getRole() != Role.ADMIN) {
            throw new AuthenticationException("No tienes permisos para eliminar usuarios.");
        }

        // You cannot delete a user with a role equal to or higher than yours
        if (!canDelete(authenticatedUser.getRole(), target.getRole())) {
            throw new AuthenticationException("No tienes permisos para eliminar a este usuario.");
        }

        deleteUserAssets(target);
        userRepository.delete(target);
    }

    @Override
    public void setPasswordByAdmin(Long userId, String newPassword) {
        User user = getById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // --- Private helper methods ---
    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    private boolean canAssign(Role assignerRole, Role targetRole) {
        return switch (assignerRole) {
            case SUPER_ADMIN ->
                    targetRole == Role.ADMIN || targetRole == Role.PROVIDER || targetRole == Role.USER;
            case ADMIN ->
                    targetRole == Role.PROVIDER || targetRole == Role.USER;
            default ->
                    false;
        };
    }

    private boolean canDelete(Role deleterRole, Role targetRole) {
        return switch (deleterRole) {
            case SUPER_ADMIN ->
                    targetRole != Role.SUPER_ADMIN;
            case ADMIN ->
                    targetRole == Role.PROVIDER || targetRole == Role.USER;
            default ->
                    false;
        };
    }

    private void deleteUserAssets(User user) {
        if (user.getPhoto() != null) {
            imageStorageService.deleteSafely(user.getPhoto().getImageId());
        }
        if (user.getWorkPhotos() != null && !user.getWorkPhotos().isEmpty()) {
            user.getWorkPhotos().forEach(photo -> imageStorageService.deleteSafely(photo.getImageId()));
        }
    }
}
