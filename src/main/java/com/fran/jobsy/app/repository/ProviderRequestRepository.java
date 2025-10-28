package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.ProviderRequest;
import com.fran.jobsy.app.enums.ProviderRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRequestRepository extends JpaRepository<ProviderRequest, Long> {

    boolean existsByUserIdAndStatus(Long userId, ProviderRequestStatus status);

    Page<ProviderRequest> findAll(Pageable pageable);

    Optional<ProviderRequest> findByUserId(Long userId);
}
