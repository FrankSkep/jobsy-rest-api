package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.conversation.ConversationDTO;
import com.fran.jobsy.app.dto.message.MessageDTO;
import com.fran.jobsy.app.entity.Conversation;
import com.fran.jobsy.app.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = MessageMapper.class)
public interface ConversationMapper {

    @Mapping(target = "bookingId", source = "booking.id")
    @Mapping(target = "userAId", source = "userA.id")
    @Mapping(target = "userBId", source = "userB.id")
    @Mapping(target = "otherUserId", expression = "java(getOtherUserId(conversation, currentUserId))")
    @Mapping(target = "otherUserName", expression = "java(getOtherUserName(conversation, currentUserId))")
    @Mapping(target = "otherUserPhotoUrl", expression = "java(getOtherUserPhotoUrl(conversation, currentUserId))")
    @Mapping(target = "lastMessage", expression = "java(getLastMessage(conversation))")
    @Mapping(target = "updatedAt", expression = "java(getUpdatedAt(conversation))")
    ConversationDTO toDTO(Conversation conversation, Long currentUserId);

    default Long getOtherUserId(Conversation conversation, Long currentUserId) {
        if (conversation.getUserA().getId().equals(currentUserId)) {
            return conversation.getUserB().getId();
        } else {
            return conversation.getUserA().getId();
        }
    }

    default String getOtherUserName(Conversation conversation, Long currentUserId) {
        if (conversation.getUserA().getId().equals(currentUserId)) {
            return conversation.getUserB().getFirstname() + " " + conversation.getUserB().getLastname();
        } else {
            return conversation.getUserA().getFirstname() + " " + conversation.getUserA().getLastname();
        }
    }

    default String getOtherUserPhotoUrl(Conversation conversation, Long currentUserId) {
        if (conversation.getUserA().getId().equals(currentUserId)) {
            return conversation.getUserB().getPhoto() != null ? conversation.getUserB().getPhoto().getUrl() : null;
        } else {
            return conversation.getUserA().getPhoto() != null ? conversation.getUserA().getPhoto().getUrl() : null;
        }
    }

    default MessageDTO getLastMessage(Conversation conversation) {
        if (conversation.getMessages() == null || conversation.getMessages().isEmpty()) {
            return null;
        }
        Optional<Message> lastMessage = conversation.getMessages().stream()
                .max(Comparator.comparing(Message::getSentAt));
        return lastMessage.map(this::toMessageDTO).orElse(null);
    }

    default LocalDateTime getUpdatedAt(Conversation conversation) {
        if (conversation.getMessages() == null || conversation.getMessages().isEmpty()) {
            return null;
        }
        Optional<Message> lastMessage = conversation.getMessages().stream()
                .max(Comparator.comparing(Message::getSentAt));
        return lastMessage.map(Message::getSentAt).orElse(null);
    }

    MessageDTO toMessageDTO(Message message);
}
