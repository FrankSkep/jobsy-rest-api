package com.fran.jobsy.app.dto.notification;

public record NotificationDTO(
        Long id,
        String title,
        String message,
        String type,
        Boolean read,
        String createdAt
) {
}
