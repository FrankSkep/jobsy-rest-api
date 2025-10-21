package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import com.fran.jobsy.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    @Query("SELECT new com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO(" +
            "a.id, a.user.id, a.weekday, a.startTime, a.endTime) " +
            "FROM AvailabilitySlot a WHERE a.user.id = :userId")
    List<AvailabilitySlotDTO> findAllByUserId(Long userId);

    Optional<AvailabilitySlot> findByUserIdAndWeekdayAndStartTimeAndEndTime(Long userId, Integer weekday, String startTime, String endTime);

    @Query("SELECT a FROM AvailabilitySlot a WHERE a.user.id = :userId AND a.weekday = :weekday " +
            "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    Optional<AvailabilitySlot> findOverlappingSlot(Long userId, Integer weekday, String startTime, String endTime);

    List<AvailabilitySlot> findAllByUser(User user);
}