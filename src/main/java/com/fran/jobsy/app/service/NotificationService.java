package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getMyNotifications();

    void markAsRead(@PathVariable Long id);
}
