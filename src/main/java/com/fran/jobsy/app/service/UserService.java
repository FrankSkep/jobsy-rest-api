package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.auth.PasswordRequest;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.enums.Role;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    void updateUser(UserInfoUpdateRequest user);

    void updateUser(ProviderInfoUpdateRequest user);

    void updateRole(Long userId, Role role);

    void deleteUser(Long id);

    void deleteUser(String username);

    void updatePassword(String username, PasswordRequest password);

    UserPublicDTO getUser(Long id);

    UserFullDTO getUserInfo();

    void deleteMyAccount();
}
