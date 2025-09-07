package com.fran.jobsy.app.entity;

import com.fran.jobsy.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provider_profiles")
public class ProviderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1:1 Relationship with User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String bio;

    private Double hourlyRate;

    private Integer yearsExperience;

    private Double avgRatingCache;

    private String addressText;

    private Double lat;

    private Double lng;

    private Double serviceRadiusKm;

    private Boolean verifiedCert;

    // Services offered
    @OneToMany(mappedBy = "providerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services;

    // Availability schedules
    @OneToMany(mappedBy = "providerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilitySlot> availabilitySlots;

    // Provider photos
    @OneToMany(mappedBy = "providerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderPhoto> photos;

    // Certifications
    @OneToMany(mappedBy = "providerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications;

    // Received bookings
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}