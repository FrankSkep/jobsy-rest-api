package com.fran.jobsy.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "offering_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferingPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String imageId; // reference to the image in the cloud storage

    @NotBlank
    private String url; // public URL to access the image

    @ManyToOne
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;
}

