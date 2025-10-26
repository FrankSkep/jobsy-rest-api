package com.fran.jobsy.app.dto.conversation;

import com.fran.jobsy.app.dto.message.MessageDTO;

import java.time.LocalDateTime;

public record ConversationDTO(
        Long id,
        Long bookingId,
        Long userAId,
        Long userBId,
        Long otherUserId,
        String otherUserName,
        String otherUserPhotoUrl,
        MessageDTO lastMessage,
        LocalDateTime updatedAt
) {
}
