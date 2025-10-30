package com.fran.jobsy.app.entity;

import com.fran.jobsy.app.enums.ProviderRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "provider_applies")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "providerRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProviderDocument> documents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderRequestStatus status; // PENDING, APPROVED, REJECTED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    private String rejectionReason;

    // provider information
    private String bio;
    private String addressText;
    private String rfcHomoclave;
    private String curp;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}