package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.entity.Notification;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.NotificationType;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.NotificationRepository;
import com.fran.jobsy.app.service.MailService;
import com.fran.jobsy.app.service.NotificationService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MailService emailService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public List<NotificationDTO> getMyNotifications() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return notificationRepository.findAllByRecipientId(userId);
    }

    @Override
    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Override
    @Transactional
    public void notifyUser(User recipient, String title, String message, NotificationType type, boolean sendEmail) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        if (sendEmail) {
            emailService.sendEmail(recipient.getUsername(), title, message);
        }

        NotificationDTO dto = new NotificationDTO(notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType().toString(),
                notification.getRead(),
                notification.getCreatedAt().toString());

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                recipient.getId().toString(),
                "/queue/notifications",
                dto
        );
    }

    public void sendTestNotificationToAuthUser() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        NotificationDTO dto = new NotificationDTO(
                null,
                "Notificación de prueba",
                "Este es un mensaje de prueba para verificar notificaciones en tiempo real.",
                NotificationType.SYSTEM.toString(),
                false,
                Instant.now().toString()
        );

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", dto);
    }
}
