package com.fran.jobsy.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "user_photos")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String imageId; // reference to the image in the cloud storage

    @NotBlank
    private String url; // public URL to access the image

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
