package com.fran.jobsy.app.entity;

import com.fran.jobsy.app.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private Double amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String gateway; // STRIPE / PAYPAL

    private String externalRef;

    private LocalDateTime createdAt;
}

