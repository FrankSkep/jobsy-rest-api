package com.fran.jobsy.app.entity;

import com.fran.jobsy.app.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Double priceAtBooking;

    private String addressText;
    private Double lat;
    private Double lng;

    private String statusComment;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Review review;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Conversation conversation;
}

