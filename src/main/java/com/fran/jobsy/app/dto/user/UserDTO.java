package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;

public record UserDTO(
        Long id,
        String username,
        String lastname,
        String firstname,
        String country,
        Role role
) { }