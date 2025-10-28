package com.fran.jobsy.app.service.conversation;

import com.fran.jobsy.app.dto.conversation.ConversationResponse;
import com.fran.jobsy.app.dto.message.MessageResponse;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.Conversation;
import com.fran.jobsy.app.entity.Message;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.repository.ConversationRepository;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public Conversation createIfNotExists(Booking booking) {
        return conversationRepository.findByBookingId(booking.getId())
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setUserA(booking.getClient());
                    conversation.setUserB(booking.getProvider());
                    conversation.setBooking(booking);
                    return conversationRepository.save(conversation);
                });
    }

    public List<ConversationResponse> getMyConversations() {
        Long authUserId = authenticatedUserProvider.getAuthenticatedUserId();

        List<Conversation> conversations = conversationRepository.findByUserId(authUserId);

        return conversations.stream().map(conv -> {
            User other = conv.getUserA().getId().equals(authUserId)
                    ? conv.getUserB()
                    : conv.getUserA();

            Message lastMsg = conv.getMessages().isEmpty()
                    ? null
                    : conv.getMessages().getLast();

            MessageResponse lastMessageResponse = (lastMsg != null)
                    ? new MessageResponse(
                    lastMsg.getId(),
                    conv.getId(),
                    lastMsg.getSender().getId(),
                    other.getId(),
                    lastMsg.getContent(),
                    lastMsg.getSentAt(),
                    lastMsg.getRead()
            )
                    : null;

            return new ConversationResponse(
                    conv.getId(),
                    conv.getBooking() != null ? conv.getBooking().getId() : null,
                    conv.getUserA().getId(),
                    conv.getUserB().getId(),
                    other.getId(),
                    other.getFirstname() + " " + other.getLastname(),
                    other.getPhoto() != null ? other.getPhoto().getUrl() : null,
                    lastMessageResponse,
                    lastMsg != null ? lastMsg.getSentAt() : conv.getMessages().isEmpty() ? null : conv.getMessages().get(0).getSentAt()
            );
        }).toList();
    }
}

