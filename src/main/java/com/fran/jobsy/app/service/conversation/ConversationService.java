package com.fran.jobsy.app.service.conversation;

import com.fran.jobsy.app.dto.conversation.ConversationResponse;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.Conversation;

import java.util.List;

public interface ConversationService {
    Conversation createIfNotExists(Booking booking);

    List<ConversationResponse> getMyConversations();
}
