package com.fran.jobsy.app.dto.message;

import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        Long conversationId,
        Long senderId,
        Long recipientId,
        String content,
        LocalDateTime sentAt,
        Boolean read
) {
}
