package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.message.MessageResponse;
import com.fran.jobsy.app.dto.message.MessageRequest;
import com.fran.jobsy.app.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send/{conversationId}")
    public void sendMessage(@DestinationVariable Long conversationId, MessageRequest request, Principal principal) {
        MessageResponse savedMessage = messageService.sendMessage(conversationId, request);

        messagingTemplate.convertAndSendToUser(
                savedMessage.recipientId().toString(),
                "/queue/messages",
                savedMessage
        );
    }
}