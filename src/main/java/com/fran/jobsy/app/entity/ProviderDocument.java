package com.fran.jobsy.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provider_documents")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicId;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_request_id")
    private ProviderRequest providerRequest;
}
