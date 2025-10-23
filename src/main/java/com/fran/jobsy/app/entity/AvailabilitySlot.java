package com.fran.jobsy.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "availability_slots")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer weekday; // 1=Monday, 7=Sunday

    @Column(nullable = false)
    private LocalTime startTime; // HH:mm

    @Column(nullable = false)
    private LocalTime endTime;
}
