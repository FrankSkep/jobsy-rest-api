package com.fran.jobsy.app.service.user;

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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ReviewRepository reviewRepository;
    @Getter
    private final AuthenticatedUserProvider authenticatedUserProvider;

    // --- Private helper methods ---
    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
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

    private <T> boolean updateIfDifferent(T newValue, T currentValue, Consumer<T> setter) {
        if (newValue != null && !Objects.equals(currentValue, newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    // --- CRUD Operations ---
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllAsUserDTO();
    }

    @Override
    @Cacheable(value = "usersPublic", key = "#id")
    public UserPublicResponse getUser(Long id) {
        User user = getById(id);
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

    public void deleteMyAccount() {
        userRepository.deleteById(authenticatedUserProvider.getAuthenticatedUserId());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "usersFull", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "usersPublic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "usersBasic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()")
    })
    public void updateUser(UserPatchRequest userReq) {
        User user = getById(authenticatedUserProvider.getAuthenticatedUserId());

        boolean hasChanges = false;

        hasChanges |= updateIfDifferent(userReq.firstname(), user.getFirstname(), user::setFirstname);
        hasChanges |= updateIfDifferent(userReq.lastname(), user.getLastname(), user::setLastname);
        hasChanges |= updateIfDifferent(userReq.country(), user.getCountry(), user::setCountry);
        hasChanges |= updateIfDifferent(userReq.phone(), user.getPhone(), user::setPhone);

        if (hasChanges) {
            userRepository.save(user);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "usersFull", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "usersPublic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
            @CacheEvict(value = "usersBasic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()")
    })
    public void updateUser(UserFullPatchRequest userReq) {
        User user = getById(authenticatedUserProvider.getAuthenticatedUserId());

        boolean hasChanges = false;

        hasChanges |= updateIfDifferent(userReq.bio(), user.getBio(), user::setBio);
        hasChanges |= updateIfDifferent(userReq.addressText(), user.getAddressText(), user::setAddressText);
        hasChanges |= updateIfDifferent(userReq.lat(), user.getLat(), user::setLat);
        hasChanges |= updateIfDifferent(userReq.lng(), user.getLng(), user::setLng);
        hasChanges |= updateIfDifferent(userReq.serviceRadiusKm(), user.getServiceRadiusKm(), user::setServiceRadiusKm);

        if (hasChanges) {
            userRepository.save(user);
        }
    }

    // --- Admin Operations ---
    @Override
    @CachePut(value = "users", key = "#userId")
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
    @CacheEvict(value = "users", key = "#id")
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

        userRepository.delete(target);
    }

    @Override
    public void setPasswordByAdmin(Long userId, String newPassword) {
        User user = getById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
