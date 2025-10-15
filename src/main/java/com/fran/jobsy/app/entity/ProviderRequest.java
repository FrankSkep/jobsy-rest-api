package com.fran.jobsy.app.entity;

import com.fran.jobsy.app.enums.ProviderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "provider_applies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "providerRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderDocument> documents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderStatus status; // PENDING, APPROVED, REJECTED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Detailed provider information
    private String bio;
    private Double hourlyRate;
    private Integer yearsExperience;
    private String addressText;
    private Double lat;
    private Double lng;
    private Double serviceRadiusKm;
    private String rfcHomoclave;
    private Boolean verifiedCert;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}