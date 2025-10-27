package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Operaciones sobre notificaciones.")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Obtener mis notificaciones (paginadas)")
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getMyNotifications(page, size));
    }

    @Operation(summary = "Obtener cantidad de no leídas")
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }

    @Operation(summary = "Marcar notificación como leída")
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@Parameter(description = "ID de la notificación") @PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Marcar todas como leídas")
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar notificación")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@Parameter(description = "ID de la notificación") @PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar notificación de prueba")
    @PostMapping("/test")
    public ResponseEntity<Void> sendTestNotification() {
        notificationService.sendTestNotificationToAuthUser();
        return ResponseEntity.noContent().build();
    }
}
