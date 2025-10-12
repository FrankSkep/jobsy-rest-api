package com.fran.jobsy.app.dto.user;

import java.time.LocalDateTime;

public record UserPhotoDTO(
        Long id,
        String imageId,
        String url
) {
}
