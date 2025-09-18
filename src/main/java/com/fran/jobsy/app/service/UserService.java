package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.user.ProviderProfileRequest;
import com.fran.jobsy.app.dto.auth.PasswordRequest;
import com.fran.jobsy.app.dto.service.ServiceDTO;
import com.fran.jobsy.app.dto.user.UserDTO;
import com.fran.jobsy.app.dto.user.UserFullDTO;
import com.fran.jobsy.app.dto.user.UserPublicDTO;
import com.fran.jobsy.app.dto.user.UserRequest;
import com.fran.jobsy.app.enums.Role;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    void updateUser(UserRequest user);

    void updateRole(Long userId, Role role);

    void deleteUser(Long id);

    void deleteUser(String username);

    void updatePassword(String username, PasswordRequest password);

    UserPublicDTO getUser(Long id);

    UserFullDTO getUserInfo();

    void updateProviderInfo(ProviderProfileRequest request);

    List<ServiceDTO> getUserServices(Long userId);
}
