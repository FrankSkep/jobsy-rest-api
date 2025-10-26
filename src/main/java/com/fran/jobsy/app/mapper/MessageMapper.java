package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.message.MessageDTO;
import com.fran.jobsy.app.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "recipientId", expression = "java(getRecipientId(message))")
    MessageDTO toMessageDTO(Message message);

    default Long getRecipientId(Message message) {
        return message.getConversation().getUserA().getId().equals(message.getSender().getId())
                ? message.getConversation().getUserB().getId()
                : message.getConversation().getUserA().getId();
    }
}
