package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.NotificationType;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getMyNotifications();

    void markAsRead(@PathVariable Long id);

    void notifyUser(User recipient, String title, String message, NotificationType type, boolean sendEmail);
}
