package com.fran.jobsy.app.dto.notification;

import com.fran.jobsy.app.enums.NotificationType;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        Boolean read,
        String createdAt
) {
}
