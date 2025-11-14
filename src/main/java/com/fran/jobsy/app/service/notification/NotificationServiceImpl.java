package com.fran.jobsy.app.service.notification;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.notification.NotificationResponse;
import com.fran.jobsy.app.entity.Notification;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.NotificationType;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.NotificationRepository;
import com.fran.jobsy.app.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MailService emailService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Page<NotificationResponse> getMyNotifications(int page, int size) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(n -> new NotificationResponse(
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
    public void notifyUser(User recipient, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        // send email if applicable
        if (type.isSendEmail()) {
            try {
                emailService.sendEmail(recipient.getUsername(), title, message);
            } catch (
                    Exception e) {
                log.warn("No se pudo enviar el email a {}", recipient.getUsername(), e);
            }
        }

        // real-time notification via WebSocket
        sendRealtimeNotification(recipient, notification);
    }

    @Override
    public void sendRealtimeNotification(User recipient, String title, String message, NotificationType type) {
        NotificationResponse dto = new NotificationResponse(
                null,
                title,
                message,
                type,
                false,
                Instant.now().toString()
        );

        messagingTemplate.convertAndSendToUser(
                recipient.getId().toString(),
                "/queue/notifications",
                dto
        );
    }

    private void sendRealtimeNotification(User recipient, Notification notification) {
        NotificationResponse dto = new NotificationResponse(
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

        NotificationResponse dto = new NotificationResponse(
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
