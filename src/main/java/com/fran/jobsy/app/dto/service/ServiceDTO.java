package com.fran.jobsy.app.dto.service;

import com.fran.jobsy.app.dto.user.UserServiceDTO;

public record ServiceDTO(
        Long id,
        UserServiceDTO user,
        String category,
        String title,
        String description,
        Double basePrice
) {}
