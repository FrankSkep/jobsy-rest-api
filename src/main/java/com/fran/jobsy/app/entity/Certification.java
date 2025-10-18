package com.fran.jobsy.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certifications")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;
    private String issuer;
    private Integer year;
}
