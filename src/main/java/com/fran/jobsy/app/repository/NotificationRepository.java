package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT new com.fran.jobsy.app.dto.notification.NotificationDTO(" +
            "n.id, n.title, n.message, CAST(n.type AS string), n.read, CAST(n.createdAt AS string)) " +
            "FROM Notification n WHERE n.recipient.id = :recipientId ORDER BY n.createdAt DESC")
    List<NotificationDTO> findAllByRecipientId(@Param("recipientId") Long recipientId);
}
