package com.fran.jobsy.app.entity;

import com.fran.jobsy.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Client participating
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // Provider participating
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    // Relationship with appointment/booking (optional, to link chat with Booking)
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;
}