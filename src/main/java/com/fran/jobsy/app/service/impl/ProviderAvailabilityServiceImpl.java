package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.booking.AvailabilityCheckResponse;
import com.fran.jobsy.app.dto.booking.DailyAvailabilityResponse;
import com.fran.jobsy.app.dto.booking.SlotDTO;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.BookingStatus;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.AvailabilitySlotRepository;
import com.fran.jobsy.app.repository.BookingRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.ProviderAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderAvailabilityServiceImpl implements ProviderAvailabilityService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilitySlotRepository slotRepository;

    public AvailabilityCheckResponse checkAvailability(Long providerId, LocalDateTime startsAt, LocalDateTime endsAt) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        // 1️. Verify working hours
        List<AvailabilitySlot> slots = slotRepository.findAllByUser(provider);
        boolean withinSchedule = slots.stream()
                .anyMatch(slot -> slot.getWeekday() == startsAt.getDayOfWeek().getValue()
                        && !startsAt.toLocalTime().isBefore(LocalTime.parse(slot.getStartTime()))
                        && !endsAt.toLocalTime().isAfter(LocalTime.parse(slot.getEndTime())));

        if (!withinSchedule) {
            return new AvailabilityCheckResponse(false, "El proveedor no trabaja en ese horario.");
        }

        // 2️. Check for overlapping bookings
        boolean overlaps = bookingRepository.existsByProviderAndStatusInAndStartsAtLessThanAndEndsAtGreaterThan(
                provider,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                endsAt,
                startsAt
        );

        if (overlaps) {
            return new AvailabilityCheckResponse(false, "El proveedor ya tiene otra reserva en ese horario.");
        }

        return new AvailabilityCheckResponse(true, "El proveedor está disponible en ese horario.");
    }


    public AvailabilityCheckResponse checkDayAvailability(Long providerId, LocalDate date) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));

        int weekday = date.getDayOfWeek().getValue();
        List<AvailabilitySlot> slots = slotRepository.findAllByUser(provider).stream()
                .filter(s -> s.getWeekday() == weekday)
                .toList();

        // 1️. No trabaja ese día
        if (slots.isEmpty()) {
            return new AvailabilityCheckResponse(false, "El proveedor no trabaja ese día.");
        }

        // 2️. Traer todas las reservas activas (PENDING o CONFIRMED) para ese día
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Booking> bookings = bookingRepository.findByProviderAndStatusInAndStartsAtBetween(
                provider,
                List.of(BookingStatus.CONFIRMED),
                startOfDay,
                endOfDay
        );

        // 3️. Evaluar si al menos hay un hueco libre
        boolean hasFreeSlot = false;

        for (AvailabilitySlot slot : slots) {
            LocalTime current = LocalTime.parse(slot.getStartTime());
            LocalTime end = LocalTime.parse(slot.getEndTime());

            while (current.isBefore(end)) {
                LocalDateTime startTime = date.atTime(current);
                LocalDateTime endTime = date.atTime(current.plusHours(1));

                boolean overlaps = bookings.stream().anyMatch(b ->
                        b.getStartsAt().isBefore(endTime) && b.getEndsAt().isAfter(startTime)
                );

                if (!overlaps) {
                    hasFreeSlot = true;
                    break;
                }
                current = current.plusHours(1);
            }

            if (hasFreeSlot) break;
        }

        if (hasFreeSlot) {
            return new AvailabilityCheckResponse(true, "El proveedor tiene disponibilidad ese día.");
        } else {
            return new AvailabilityCheckResponse(false, "El proveedor no tiene espacios libres ese día.");
        }
    }

    public DailyAvailabilityResponse getAvailabilityForDay(Long providerId, LocalDate date) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));

        int weekday = date.getDayOfWeek().getValue();
        List<AvailabilitySlot> slots = slotRepository.findAllByUser(provider).stream()
                .filter(s -> s.getWeekday() == weekday)
                .toList();

        if (slots.isEmpty()) {
            return new DailyAvailabilityResponse(date, List.of());
        }

        // Bring all bookings for the day in a single query
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Booking> bookings = bookingRepository.findByProviderAndStatusInAndStartsAtBetween(
                provider,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                startOfDay,
                endOfDay
        );

        List<SlotDTO> availableSlots = new ArrayList<>();

        // Calculate one-hour intervals based on availability slots
        for (AvailabilitySlot slot : slots) {
            LocalTime current = LocalTime.parse(slot.getStartTime());
            LocalTime end = LocalTime.parse(slot.getEndTime());

            while (current.isBefore(end)) {
                LocalDateTime startTime = date.atTime(current);
                LocalDateTime endTime = date.atTime(current.plusHours(1));

                // Check if it overlaps with any booking
                boolean overlaps = bookings.stream().anyMatch(b ->
                        b.getStartsAt().isBefore(endTime) && b.getEndsAt().isAfter(startTime)
                );

                availableSlots.add(new SlotDTO(current.toString(), current.plusHours(1).toString(), !overlaps));

                current = current.plusHours(1);
            }
        }
        return new DailyAvailabilityResponse(date, availableSlots);
    }
}
