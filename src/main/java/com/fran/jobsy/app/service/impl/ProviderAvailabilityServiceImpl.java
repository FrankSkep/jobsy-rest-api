package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
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

    private static final int MAX_MONTHS_AHEAD = 6;

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilitySlotRepository slotRepository;

    @Override
    public AvailabilityCheckResponse checkAvailability(Long providerId, LocalDateTime startsAt, LocalDateTime endsAt) {
        validateDateRange(startsAt, endsAt);
        validateMaxAdvance(startsAt);

        User provider = findProvider(providerId);
        List<AvailabilitySlot> slots = slotRepository.findAllByUser(provider);
        if (slots.isEmpty()) {
            return new AvailabilityCheckResponse(false, "El proveedor aun no ha definido su horario de trabajo.");
        }

        int requestedDay = startsAt.getDayOfWeek().getValue();
        List<AvailabilitySlot> daySlots = getSlotsForDay(slots, requestedDay);

        if (daySlots.isEmpty()) {
            return new AvailabilityCheckResponse(false, "El proveedor no trabaja ese día.");
        }

        if (!isWithinSchedule(daySlots, startsAt.toLocalTime(), endsAt.toLocalTime())) {
            return new AvailabilityCheckResponse(false, "El proveedor no trabaja en ese horario.");
        }

        boolean overlaps = bookingRepository.existsByProviderAndStatusInAndStartsAtLessThanAndEndsAtGreaterThan(
                provider, List.of(BookingStatus.CONFIRMED), endsAt, startsAt
        );

        if (overlaps) {
            return new AvailabilityCheckResponse(false, "El proveedor ya tiene otra reserva en ese horario.");
        }

        return new AvailabilityCheckResponse(true, "El proveedor está disponible en ese horario.");
    }

    @Override
    public AvailabilityCheckResponse checkDayAvailability(Long providerId, LocalDate date) {
        checkPastDate(date);
        validateMaxAdvance(date.atStartOfDay());

        User provider = findProvider(providerId);
        int weekday = date.getDayOfWeek().getValue();

        List<AvailabilitySlot> slots = getSlotsForDay(slotRepository.findAllByUser(provider), weekday);
        if (slots.isEmpty()) {
            return new AvailabilityCheckResponse(false, "El proveedor no trabaja ese día.");
        }

        List<Booking> bookings = findBookingsForDay(provider, date);

        boolean hasFreeSlot = slots.stream()
                .anyMatch(slot -> hasFreeHourInSlot(slot, date, bookings));

        return hasFreeSlot
                ? new AvailabilityCheckResponse(true, "El proveedor tiene disponibilidad ese día.")
                : new AvailabilityCheckResponse(false, "El proveedor no tiene espacios libres ese día.");
    }

    @Override
    public DailyAvailabilityResponse getAvailabilityForDay(Long providerId, LocalDate date) {
        checkPastDate(date);
        validateMaxAdvance(date.atStartOfDay());

        User provider = findProvider(providerId);
        int weekday = date.getDayOfWeek().getValue();

        List<AvailabilitySlot> slots = getSlotsForDay(slotRepository.findAllByUser(provider), weekday);
        if (slots.isEmpty()) {
            return new DailyAvailabilityResponse(date, List.of());
        }

        List<Booking> bookings = findBookingsForDay(provider, date);
        List<SlotDTO> availableSlots = buildSlotDTOs(slots, date, bookings);

        return new DailyAvailabilityResponse(date, availableSlots);
    }

    // ----- Private Helpers -----

    private User findProvider(Long providerId) {
        return userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));
    }

    private void validateDateRange(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (!startsAt.isBefore(endsAt)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        checkPastDate(startsAt);
        checkPastDate(endsAt);
    }

    private void validateMaxAdvance(LocalDateTime dateTime) {
        if (dateTime.isAfter(LocalDateTime.now().plusMonths(MAX_MONTHS_AHEAD))) {
            throw new IllegalArgumentException("No se puede consultar disponibilidad con tanta anticipación.");
        }
    }

    private List<AvailabilitySlot> getSlotsForDay(List<AvailabilitySlot> slots, int weekday) {
        return slots.stream()
                .filter(slot -> slot.getWeekday() != null && slot.getWeekday() == weekday)
                .toList();
    }

    private boolean isWithinSchedule(List<AvailabilitySlot> daySlots, LocalTime startTime, LocalTime endTime) {
        LocalTime earliestStart = daySlots.stream()
                .map(AvailabilitySlot::getStartTime)
                .min(LocalTime::compareTo)
                .orElseThrow();

        LocalTime latestEnd = daySlots.stream()
                .map(AvailabilitySlot::getEndTime)
                .max(LocalTime::compareTo)
                .orElseThrow();

        return !startTime.isBefore(earliestStart) && !endTime.isAfter(latestEnd);
    }

    private List<Booking> findBookingsForDay(User provider, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return bookingRepository.findByProviderAndStatusInAndStartsAtLessThanAndEndsAtGreaterThan(
                provider, List.of(BookingStatus.CONFIRMED), endOfDay, startOfDay
        );
    }

    private boolean hasFreeHourInSlot(AvailabilitySlot slot, LocalDate date, List<Booking> bookings) {
        LocalTime current = slot.getStartTime();
        LocalTime end = slot.getEndTime();

        while (current.isBefore(end)) {
            LocalDateTime startTime = date.atTime(current);
            LocalDateTime endTime = date.atTime(current.plusHours(1));

            boolean overlaps = bookings.stream().anyMatch(b ->
                    b.getStartsAt().isBefore(endTime) && b.getEndsAt().isAfter(startTime)
            );

            if (!overlaps) return true;
            current = current.plusHours(1);
        }
        return false;
    }

    private List<SlotDTO> buildSlotDTOs(List<AvailabilitySlot> slots, LocalDate date, List<Booking> bookings) {
        List<SlotDTO> availableSlots = new ArrayList<>();

        for (AvailabilitySlot slot : slots) {
            LocalTime current = slot.getStartTime();
            LocalTime end = slot.getEndTime();

            while (current.isBefore(end)) {
                LocalDateTime startTime = date.atTime(current);
                LocalDateTime endTime = date.atTime(current.plusHours(1));

                boolean overlaps = bookings.stream().anyMatch(b ->
                        b.getStartsAt().isBefore(endTime) && b.getEndsAt().isAfter(startTime)
                );

                availableSlots.add(new SlotDTO(
                        current.toString(),
                        current.plusHours(1).toString(),
                        !overlaps
                ));
                current = current.plusHours(1);
            }
        }

        return availableSlots;
    }

    private void checkPastDate(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes elegir una fecha pasada.");
        }
    }

    private void checkPastDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No puedes elegir una fecha pasada.");
        }
    }
}
