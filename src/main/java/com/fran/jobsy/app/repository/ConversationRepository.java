package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByBookingId(Long bookingId);

    @Query("""
                SELECT c FROM Conversation c
                WHERE c.userA.id = :userId
                   OR c.userB.id = :userId
                ORDER BY (
                    SELECT MAX(m.sentAt)
                    FROM Message m
                    WHERE m.conversation = c
                ) DESC
            """)
    List<Conversation> findByUserId(@Param("userId") Long userId);
}
