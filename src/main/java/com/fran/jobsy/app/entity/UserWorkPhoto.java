package com.fran.jobsy.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_work_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWorkPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String imageId;

    @NotBlank
    private String url;

    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

