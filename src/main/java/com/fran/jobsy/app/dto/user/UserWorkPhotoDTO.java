package com.fran.jobsy.app.dto.user;

import java.time.LocalDateTime;

public record UserWorkPhotoDTO(
        Long id,
        String url,
        LocalDateTime uploadedAt
) {
}
