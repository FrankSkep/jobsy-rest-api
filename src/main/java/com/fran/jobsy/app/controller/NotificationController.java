package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Obtener mis notificaciones", description = "Devuelve la lista de notificaciones del usuario autenticado.")
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @Operation(summary = "Marcar notificación como leída", description = "Marca una notificación específica como leída por su ID.")
    @PostMapping("/{id}/read")
    public void markAsRead(@Parameter(description = "ID de la notificación a marcar como leída", required = true)
                           @PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
