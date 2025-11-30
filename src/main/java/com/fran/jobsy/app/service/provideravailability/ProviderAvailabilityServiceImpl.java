package com.fran.jobsy.app.service.provideravailability;

import com.fran.jobsy.app.dto.booking.AvailabilityCheckResponse;
import com.fran.jobsy.app.dto.booking.DailyAvailabilityResponse;
import com.fran.jobsy.app.dto.booking.SlotResponse;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.BookingStatus;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.AvailabilitySlotRepository;
import com.fran.jobsy.app.repository.BookingRepository;
import com.fran.jobsy.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderAvailabilityServiceImpl implements ProviderAvailabilityService {

    private static final int MAX_MONTHS_AHEAD = 6;
    private static final String NO_SCHEDULE = "El proveedor aun no ha definido su horario de trabajo.";
    private static final String NO_WORK_DAY = "El proveedor no trabaja ese día.";
    private static final String NO_WORK_HOURS = "El proveedor no trabaja en ese horario.";
    private static final String ALREADY_BOOKED = "El proveedor ya tiene otra reserva en ese horario.";
    private static final String NO_FREE_SLOTS = "El proveedor no tiene espacios libres ese día.";

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilitySlotRepository slotRepository;

    // Valida si un proveedor está disponible en un rango de horas específico
    @Override
    public AvailabilityCheckResponse checkAvailability(Long providerId, LocalDateTime startsAt, LocalDateTime endsAt) {
        // 1. Validar fechas
        validateBookingTime(startsAt, endsAt);

        // 2. Obtener proveedor y su horario
        User provider = getProvider(providerId);
        List<AvailabilitySlot> providerSchedule = slotRepository.findAllByUser(provider);

        if (providerSchedule.isEmpty()) {
            return unavailable(NO_SCHEDULE);
        }

        // 3. Verificar si trabaja ese día
        List<AvailabilitySlot> daySchedule = getScheduleForDay(providerSchedule, startsAt);
        if (daySchedule.isEmpty()) {
            return unavailable(NO_WORK_DAY);
        }

        // 4. Verificar si trabaja en ese horario
        if (!isWithinWorkingHours(daySchedule, startsAt.toLocalTime(), endsAt.toLocalTime())) {
            return unavailable(NO_WORK_HOURS);
        }

        // 5. Verificar si ya tiene reserva
        if (hasBookingConflict(provider, startsAt, endsAt)) {
            return unavailable(ALREADY_BOOKED);
        }

        return available("El proveedor está disponible en ese horario.");
    }

    // Valida si un proveedor tiene disponibilidad en algún momento de un día específico
    @Override
    public AvailabilityCheckResponse checkDayAvailability(Long providerId, LocalDate date) {
        // 1. Validar fecha
        validateDate(date);

        // 2. Obtener proveedor y su horario
        User provider = getProvider(providerId);
        List<AvailabilitySlot> providerSchedule = slotRepository.findAllByUser(provider);

        if (providerSchedule.isEmpty()) {
            return unavailable(NO_SCHEDULE);
        }

        // 3. Verificar si trabaja ese día
        List<AvailabilitySlot> daySchedule = getScheduleForDay(providerSchedule, date);
        if (daySchedule.isEmpty()) {
            return unavailable(NO_WORK_DAY);
        }

        // 4. Verificar si tiene al menos 1 hora libre
        List<Booking> dayBookings = getBookingsForDay(provider, date);
        boolean hasFreeSlot = daySchedule.stream()
                .anyMatch(slot -> hasAvailableHour(slot, date, dayBookings));

        return hasFreeSlot
                ? available("El proveedor tiene disponibilidad ese día.")
                : unavailable(NO_FREE_SLOTS);
    }

    // Obtiene todos los slots horarios disponibles de un proveedor para un día específico
    @Override
    public DailyAvailabilityResponse getAvailabilityForDay(Long providerId, LocalDate date) {
        // 1. Validar fecha
        validateDate(date);

        // 2. Obtener horario del día
        User provider = getProvider(providerId);
        List<AvailabilitySlot> daySchedule = getScheduleForDay(
                slotRepository.findAllByUser(provider), date);

        if (daySchedule.isEmpty()) {
            return new DailyAvailabilityResponse(date, List.of());
        }

        // 3. Generar slots de 1 hora con disponibilidad
        List<Booking> dayBookings = getBookingsForDay(provider, date);
        List<SlotResponse> hourlySlots = generateHourlySlots(daySchedule, date, dayBookings);

        return new DailyAvailabilityResponse(date, hourlySlots);
    }

    // ========== VALIDACIONES ==========

    // Valida que el rango de tiempo sea lógico y dentro del rango permitido
    private void validateBookingTime(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        validateDate(start);
        validateDate(end);
    }

    // Valida que la fecha no sea pasada ni demasiado lejana
    private void validateDate(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes elegir una fecha pasada.");
        }
        if (dateTime.isAfter(LocalDateTime.now().plusMonths(MAX_MONTHS_AHEAD))) {
            throw new IllegalArgumentException("No se puede consultar disponibilidad con tanta anticipación.");
        }
    }

    // Convierte un LocalDate a LocalDateTime para validarlo
    private void validateDate(LocalDate date) {
        validateDate(date.atStartOfDay());
    }

    // ========== CONSULTAS ==========

    // Obtiene un proveedor por ID o lanza excepción si no existe
    private User getProvider(Long providerId) {
        return userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));
    }

    // Filtra el horario del proveedor por día de la semana usando fecha con hora
    private List<AvailabilitySlot> getScheduleForDay(List<AvailabilitySlot> schedule, LocalDateTime dateTime) {
        return getScheduleForDay(schedule, dateTime.getDayOfWeek().getValue());
    }

    // Filtra el horario del proveedor por día de la semana usando fecha sin hora
    private List<AvailabilitySlot> getScheduleForDay(List<AvailabilitySlot> schedule, LocalDate date) {
        return getScheduleForDay(schedule, date.getDayOfWeek().getValue());
    }

    // Filtra el horario del proveedor según un número de día de la semana
    private List<AvailabilitySlot> getScheduleForDay(List<AvailabilitySlot> schedule, int weekday) {
        return schedule.stream()
                .filter(slot -> slot.getWeekday() != null && slot.getWeekday() == weekday)
                .toList();
    }

    // Obtiene todas las reservas confirmadas de un proveedor en un día
    private List<Booking> getBookingsForDay(User provider, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        return bookingRepository.findByProviderAndStatusInAndStartsAtLessThanAndEndsAtGreaterThan(
                provider, List.of(BookingStatus.CONFIRMED), dayEnd, dayStart);
    }

    // Verifica si un proveedor ya tiene una reserva que se cruce con un horario dado
    private boolean hasBookingConflict(User provider, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.existsByProviderAndStatusInAndStartsAtLessThanAndEndsAtGreaterThan(
                provider, List.of(BookingStatus.CONFIRMED), end, start);
    }

    // ========== LÓGICA DE DISPONIBILIDAD ==========

    // Verifica si un rango horario está dentro del horario laboral del proveedor
    private boolean isWithinWorkingHours(List<AvailabilitySlot> daySchedule, LocalTime start, LocalTime end) {
        LocalTime earliestStart = daySchedule.stream()
                .map(AvailabilitySlot::getStartTime)
                .min(LocalTime::compareTo)
                .orElseThrow();

        LocalTime latestEnd = daySchedule.stream()
                .map(AvailabilitySlot::getEndTime)
                .max(LocalTime::compareTo)
                .orElseThrow();

        return !start.isBefore(earliestStart) && !end.isAfter(latestEnd);
    }

    // Verifica si existe al menos una hora libre dentro de un tramo de horario
    private boolean hasAvailableHour(AvailabilitySlot slot, LocalDate date, List<Booking> bookings) {
        LocalTime current = slot.getStartTime();

        while (current.isBefore(slot.getEndTime())) {
            if (isHourAvailable(date, current, bookings)) {
                return true;
            }
            current = current.plusHours(1);
        }
        return false;
    }

    // Verifica si una hora específica está libre comparándola contra las reservas existentes
    private boolean isHourAvailable(LocalDate date, LocalTime hour, List<Booking> bookings) {
        LocalDateTime slotStart = date.atTime(hour);
        LocalDateTime slotEnd = date.atTime(hour.plusHours(1));

        return bookings.stream().noneMatch(booking ->
                booking.getStartsAt().isBefore(slotEnd) &&
                        booking.getEndsAt().isAfter(slotStart));
    }

    // Genera los slots de una hora con su respectiva disponibilidad
    private List<SlotResponse> generateHourlySlots(List<AvailabilitySlot> daySchedule,
                                                   LocalDate date,
                                                   List<Booking> bookings) {
        List<SlotResponse> slots = new ArrayList<>();

        for (AvailabilitySlot scheduleSlot : daySchedule) {
            LocalTime current = scheduleSlot.getStartTime();

            while (current.isBefore(scheduleSlot.getEndTime())) {
                boolean available = isHourAvailable(date, current, bookings);

                slots.add(new SlotResponse(
                        current.toString(),
                        current.plusHours(1).toString(),
                        available));

                current = current.plusHours(1);
            }
        }

        return slots;
    }

    // ========== HELPERS ==========

    // Construye una respuesta de disponibilidad positiva
    private AvailabilityCheckResponse available(String message) {
        return new AvailabilityCheckResponse(true, message);
    }

    // Construye una respuesta de disponibilidad negativa
    private AvailabilityCheckResponse unavailable(String message) {
        return new AvailabilityCheckResponse(false, message);
    }
}