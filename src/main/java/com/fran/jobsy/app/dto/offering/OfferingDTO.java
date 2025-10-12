package com.fran.jobsy.app.dto.offering;

import com.fran.jobsy.app.dto.user.UserServiceDTO;

public record OfferingDTO(
        Long id,
        UserServiceDTO user,
        String category,
        String title,
        String description,
        Double basePrice
) {
}
