package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.booking.*;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.Offering;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.BookingStatus;
import com.fran.jobsy.app.enums.NotificationType;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.mapper.BookingMapper;
import com.fran.jobsy.app.repository.BookingRepository;
import com.fran.jobsy.app.repository.OfferingRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.BookingService;
import com.fran.jobsy.app.service.ConversationService;
import com.fran.jobsy.app.service.NotificationService;
import com.fran.jobsy.app.service.ProviderAvailabilityService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final OfferingRepository offeringRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ProviderAvailabilityService providerAvailabilityService;
    private final NotificationService notificationService;
    private final BookingMapper bookingMapper;
    private final ConversationService conversationService;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequest bookingReq) {
        User provider = findUserById(bookingReq.providerId());
        Offering offering = findOfferingById(bookingReq.offeringId());
        User client = authenticatedUserProvider.getAuthenticatedUser();

        if (provider.getId().equals(client.getId())) {
            throw new ConflictException("No puedes reservar tus propios servicios");
        }

        AvailabilityCheckResponse check = providerAvailabilityService.checkAvailability(
                provider.getId(), bookingReq.startsAt(), bookingReq.endsAt());

        if (!check.available()) {
            throw new ConflictException(check.message());
        }

        Booking booking = Booking.builder()
                .client(client)
                .provider(provider)
                .offering(offering)
                .startsAt(bookingReq.startsAt())
                .endsAt(bookingReq.endsAt())
                .status(BookingStatus.PENDING)
                .priceAtBooking(bookingReq.priceAtBooking())
                .addressText(bookingReq.addressText())
                .lat(bookingReq.lat())
                .lng(bookingReq.lng())
                .build();

        bookingRepository.save(booking);

        notificationService.notifyUser(
                client,
                "Reserva solicitada con éxito.",
                "Su reserva ha sido creada y está pendiente de confirmación.",
                NotificationType.BOOKING,
                true
        );

        return bookingMapper.toBookingResponseDTO(booking);
    }

    @Override
    public List<BookingListDTO> getClientBookings() {
        return mapBookingsToDTOs(bookingRepository.findAllByClientId(authenticatedUserProvider.getAuthenticatedUserId()));
    }

    @Override
    public List<BookingListDTO> getProviderBookings() {
        return mapBookingsToDTOs(bookingRepository.findAllByProviderId(authenticatedUserProvider.getAuthenticatedUserId()));
    }

    @Override
    public BookingResponseDTO getBooking(Long id) {
        return bookingMapper.toBookingResponseDTO(
                bookingRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id))
        );
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(Long id, BookingStatusUpdateReqDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        Long authId = authenticatedUserProvider.getAuthenticatedUserId();
        switch (dto.status()) {
            case CONFIRMED ->
                    handleConfirmed(booking, authId, dto.comment());
            case CANCELED ->
                    handleCanceled(booking, authId, dto.comment());
            case COMPLETED ->
                    handleCompleted(booking, authId, dto.comment());
            default ->
                    throw new ConflictException("Estado de reserva no válido: " + dto.status());
        }

        return getBooking(id);
    }

    // --- Private Helpers ---
    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));
    }

    private Offering findOfferingById(Long id) {
        return offeringRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada."));
    }

    private List<BookingListDTO> mapBookingsToDTOs(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::toBookingListDTO).toList();
    }

    private void handleConfirmed(Booking booking, Long authId, String comment) {
        validateProviderAction(booking, authId, BookingStatus.PENDING, "confirmar la reserva");
        updateBookingStatusAndNotify(booking, BookingStatus.CONFIRMED, comment, booking.getClient(),
                "Reserva confirmada", "La reserva fue confirmada");

        // Iniciar o obtener la conversación asociada a esta reserva (si no existe)
        conversationService.createIfNotExists(booking);
    }

    private void handleCanceled(Booking booking, Long authId, String comment) {
        boolean isProvider = booking.getProvider().getId().equals(authId);
        boolean isClient = booking.getClient().getId().equals(authId);

        if (!isProvider && !isClient)
            throw new AccessDeniedException("No autorizado para cancelar la reserva.");
        if (booking.getStatus() == BookingStatus.CANCELED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ConflictException("No se puede cancelar una reserva en estado: " + booking.getStatus());
        }

        User recipient = isProvider ? booking.getClient() : booking.getProvider();
        updateBookingStatusAndNotify(booking, BookingStatus.CANCELED, comment, recipient,
                "Reserva cancelada", "La reserva fue cancelada");
    }

    private void handleCompleted(Booking booking, Long authId, String comment) {
        validateProviderAction(booking, authId, BookingStatus.CONFIRMED, "marcar la reserva como completada");
        updateBookingStatusAndNotify(booking, BookingStatus.COMPLETED, comment, booking.getClient(),
                "Reserva completada", "La reserva fue completada");
    }

    private void validateProviderAction(Booking booking, Long authId, BookingStatus expectedStatus, String action) {
        if (!booking.getProvider().getId().equals(authId))
            throw new AccessDeniedException("Solo el proveedor puede " + action + ".");
        if (booking.getStatus() != expectedStatus)
            throw new ConflictException("No se puede " + action + " una reserva en estado: " + booking.getStatus());
    }

    private void updateBookingStatusAndNotify(Booking booking, BookingStatus status, String comment,
                                              User recipient, String title, String defaultMessage) {
        booking.setStatus(status);
        booking.setStatusComment(comment);
        bookingRepository.save(booking);

        notificationService.notifyUser(
                recipient,
                title,
                comment != null ? defaultMessage + ": " + comment : defaultMessage,
                NotificationType.BOOKING,
                true
        );
    }
}
