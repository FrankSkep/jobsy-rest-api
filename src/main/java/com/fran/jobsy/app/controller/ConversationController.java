package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.conversation.ConversationDTO;
import com.fran.jobsy.app.dto.message.MessageDTO;
import com.fran.jobsy.app.dto.message.MessageRequest;
import com.fran.jobsy.app.service.ConversationService;
import com.fran.jobsy.app.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversation", description = "Endpoints for managing conversations and messages")
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    @GetMapping
    public List<ConversationDTO> listMyConversations() {
        return conversationService.getMyConversations();
    }

    @GetMapping("/{id}/messages")
    public List<MessageDTO> getMessages(@PathVariable Long id) {
        return messageService.getMessagesByConversation(id);
    }

    @PostMapping("/{id}/messages")
    public MessageDTO sendMessage(@PathVariable Long id, @RequestBody MessageRequest request) {
        return messageService.sendMessage(id, request);
    }
}
