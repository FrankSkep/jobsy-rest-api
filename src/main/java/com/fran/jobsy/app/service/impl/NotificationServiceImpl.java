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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MailService emailService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Page<NotificationDTO> getMyNotifications(int page, int size) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(n -> new NotificationDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getType(),
                        n.getRead(),
                        n.getCreatedAt().toString()
                ));
    }

    @Override
    public Long getUnreadCount() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        notificationRepository.markAllAsReadByRecipient(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        notificationRepository.delete(n);
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

        NotificationDTO dto = new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getRead(),
                notification.getCreatedAt().toString()
        );

        messagingTemplate.convertAndSendToUser(
                recipient.getId().toString(),
                "/queue/notifications",
                dto
        );
    }

    // === TEST NOTIFICATION ===
    @Override
    public void sendTestNotificationToAuthUser() {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        NotificationDTO dto = new NotificationDTO(
                null,
                "Notificación de prueba",
                "Este es un mensaje de prueba para verificar notificaciones en tiempo real.",
                NotificationType.SYSTEM,
                false,
                Instant.now().toString()
        );

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", dto);
    }
}
