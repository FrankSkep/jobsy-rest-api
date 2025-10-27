package com.fran.jobsy.app.dto.user;

import java.time.LocalDateTime;

public record UserWorkPhotoResponse(
        Long id,
        String url,
        LocalDateTime uploadedAt
) {
}
