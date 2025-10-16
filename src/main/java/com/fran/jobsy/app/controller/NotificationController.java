package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Operaciones sobre notificaciones.")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
