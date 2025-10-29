package com.fran.jobsy.app.service.user;

import com.fran.jobsy.app.dto.auth.PasswordUpdateRequest;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.enums.Role;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    void updateUser(UserUpdateRequest user);

    void updateUser(UserFullUpdateRequest user);

    void updateRole(Long userId, Role role);

    void deleteUser(Long id);

    void updatePassword(PasswordUpdateRequest password);

    void setPasswordByAdmin(Long userId, String newPassword);

    UserPublicResponse getUser(Long id);

    UserFullResponse getMyFullInfo();

    UserResponse getMyBasicInfo();

    void deleteMyAccount();
}
