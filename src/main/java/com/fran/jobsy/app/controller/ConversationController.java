package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.conversation.ConversationDTO;
import com.fran.jobsy.app.dto.message.MessageDTO;
import com.fran.jobsy.app.dto.message.MessageRequest;
import com.fran.jobsy.app.service.ConversationService;
import com.fran.jobsy.app.service.MessageService;
import com.fran.jobsy.app.util.RestUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversation", description = "Endpoints for managing conversations and messages")
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<ConversationDTO>> listMyConversations() {
        return ResponseEntity.ok(conversationService.getMyConversations());
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessagesByConversation(id));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable Long id, @RequestBody MessageRequest request) {
        MessageDTO message = messageService.sendMessage(id, request);
        URI location = RestUtils.buildCreatedLocation(message.id());
        return ResponseEntity.created(location).body(message);
    }
}
