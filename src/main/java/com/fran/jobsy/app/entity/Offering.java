package com.fran.jobsy.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "offerings")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String title;

    private String description;

    private Double basePrice;

    private Double yearsOfExperience;

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OfferingPhoto> photos;
}
