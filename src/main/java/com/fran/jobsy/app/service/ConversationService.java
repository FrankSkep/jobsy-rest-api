package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.conversation.ConversationDTO;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.Conversation;

import java.util.List;

public interface ConversationService {
    Conversation createIfNotExists(Booking booking);

    List<ConversationDTO> getMyConversations();
}
