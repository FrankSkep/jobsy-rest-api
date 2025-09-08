package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.auth.PasswordRequest;
import com.fran.jobsy.app.dto.user.UserDTO;
import com.fran.jobsy.app.dto.user.UserRequest;
import com.fran.jobsy.app.enums.Role;

public interface UserService {
    void updateUser(String username, UserRequest user);

    void updateRole(Long userId, Role role);

    void deleteUser(Long id);

    void deleteUser(String username);

    void updatePassword(String username, PasswordRequest password);

    UserDTO getUserInfoByUsername(String username);

}
