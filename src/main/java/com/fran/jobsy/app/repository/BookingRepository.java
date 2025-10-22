package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Boolean existsByProviderAndStatusInAndStartsAtLessThanAndEndsAtGreaterThan(
            User providerId,
            List<BookingStatus> statuses,
            LocalDateTime endsAt,
            LocalDateTime startsAt
    );

    List<Booking> findByProviderAndStatusInAndStartsAtBetween(User provider, Collection<BookingStatus> statuses, LocalDateTime startsAtAfter, LocalDateTime startsAtBefore);

    List<Booking> findAllByClientId(Long clientId);

    List<Booking> findByProvider_IdAndStatusIn(Long providerId, Collection<BookingStatus> statuses);
}
