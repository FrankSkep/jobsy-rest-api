package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.message.MessageDTO;
import com.fran.jobsy.app.dto.message.MessageRequest;

import java.util.List;

public interface MessageService {
    MessageDTO sendMessage(Long conversationId, MessageRequest req);

    List<MessageDTO> getMessagesByConversation(Long conversationId);
}
