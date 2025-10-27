package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.message.MessageRequest;
import com.fran.jobsy.app.dto.message.MessageResponse;

import java.util.List;

public interface MessageService {
    MessageResponse sendMessage(Long conversationId, MessageRequest req);

    List<MessageResponse> getMessagesByConversation(Long conversationId);
}
