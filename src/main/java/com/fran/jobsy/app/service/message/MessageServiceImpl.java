package com.fran.jobsy.app.service.message;

import com.fran.jobsy.app.dto.message.MessageRequest;
import com.fran.jobsy.app.dto.message.MessageResponse;
import com.fran.jobsy.app.entity.Conversation;
import com.fran.jobsy.app.entity.Message;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.mapper.MessageMapper;
import com.fran.jobsy.app.repository.ConversationRepository;
import com.fran.jobsy.app.repository.MessageRepository;
import com.fran.jobsy.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    public MessageResponse sendMessage(Long conversationId, MessageRequest req) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación no encontrada"));

        User sender = userRepository.findById(req.senderId())
                .orElseThrow(() -> new ResourceNotFoundException("Remitente no encontrado"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(req.content());

        Message saved = messageRepository.save(message);

        return messageMapper.toDTO(saved);
    }

    public List<MessageResponse> getMessagesByConversation(Long conversationId) {
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(messageMapper::toDTO)
                .toList();
    }
}

