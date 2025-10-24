package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.NotificationType;
import org.springframework.data.domain.Page;

public interface NotificationService {

    Page<NotificationDTO> getMyNotifications(int page, int size);

    void markAsRead(Long notificationId);

    void markAllAsRead();

    void deleteNotification(Long notificationId);

    Long getUnreadCount();

    void sendTestNotificationToAuthUser();

    void notifyUser(User recipient, String title, String message, NotificationType type, boolean sendEmail);
}
