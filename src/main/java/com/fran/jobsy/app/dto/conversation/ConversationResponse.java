package com.fran.jobsy.app.dto.conversation;

import com.fran.jobsy.app.dto.message.MessageResponse;

import java.time.LocalDateTime;

public record ConversationResponse(
        Long id,
        Long bookingId,
        Long userAId,
        Long userBId,
        Long otherUserId,
        String otherUserName,
        String otherUserPhotoUrl,
        String otherUserSlug,
        MessageResponse lastMessage,
        LocalDateTime updatedAt
) {
}
