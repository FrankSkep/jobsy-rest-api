package com.fran.jobsy.app.service.booking;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
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
import com.fran.jobsy.app.service.conversation.ConversationService;
import com.fran.jobsy.app.service.notification.NotificationService;
import com.fran.jobsy.app.service.provideravailability.ProviderAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    public BookingResponse createBooking(BookingRequest bookingReq) {
        User provider = findUserById(bookingReq.providerId());
        Offering offering = findOfferingById(bookingReq.offeringId());

        if (!offering.getOwner().getId().equals(provider.getId())) {
            throw new ConflictException("La oferta no pertenece al proveedor especificado.");
        }

        if (!offering.isActive()) {
            throw new ConflictException("No se puede reservar una oferta inactiva.");
        }

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
                .priceAtBooking(offering.getBasePrice())
                .addressText(bookingReq.addressText())
                .lat(bookingReq.lat())
                .lng(bookingReq.lng())
                .build();

        bookingRepository.save(booking);

        // Notify client after transaction commit
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationService.notifyUser(
                        client,
                        "Reserva solicitada con éxito.",
                        "Su reserva ha sido creada y está pendiente de confirmación.",
                        NotificationType.BOOKING_CREATED);
                notificationService.notifyUser(
                        provider, "Nueva reserva pendiente de confirmación.",
                        "Tienes una nueva reserva pendiente de confirmación de "
                                + client.getFirstname() + " " + client.getLastname() + ".",
                        NotificationType.BOOKING_CREATED
                );
            }
        });

        return bookingMapper.toDTO(booking);
    }

    @Override
    public List<BookingSummaryResponse> getClientBookings() {
        return mapBookingsToDTOs(bookingRepository.findAllByClientId(authenticatedUserProvider.getAuthenticatedUserId()));
    }

    @Override
    public List<BookingSummaryResponse> getProviderBookings() {
        return mapBookingsToDTOs(bookingRepository.findAllByProviderId(authenticatedUserProvider.getAuthenticatedUserId()));
    }

    @Override
    public BookingResponse getBooking(Long id) {
        return bookingMapper.toDTO(
                bookingRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id))
        );
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatusUpdateRequest dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        Long authId = authenticatedUserProvider.getAuthenticatedUserId();
        switch (dto.status()) {
            case CONFIRMED ->
                    handleConfirmed(booking, authId, dto.comment());
            case CANCELED ->
                    handleCanceled(booking, authId, dto.comment());
            case REJECTED ->
                    handleRejected(booking, authId, dto.comment());
            case COMPLETED ->
                    handleCompleted(booking, authId, dto.comment(), dto.finalPrice());
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

    private List<BookingSummaryResponse> mapBookingsToDTOs(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::toSummaryDTO).toList();
    }

    private void handleConfirmed(Booking booking, Long authId, String comment) {
        validateProviderAction(booking, authId, BookingStatus.PENDING, "confirmar la reserva");
        updateBookingStatusAndNotify(booking, BookingStatus.CONFIRMED, comment, booking.getClient(),
                "Reserva confirmada", "El proveedor acepto tu reserva. Ya puedes chatear con él desde la sección de mensajes.");

        // Iniciar o obtener la conversación asociada a esta reserva (si no existe)
        conversationService.createIfNotExists(booking);
    }

    private void handleCanceled(Booking booking, Long authId, String comment) {
        if (!booking.getClient().getId().equals(authId)) {
            throw new AccessDeniedException("Solo el cliente puede cancelar la reserva.");
        }

        if (booking.getStatus() == BookingStatus.CANCELED || booking.getStatus() == BookingStatus.REJECTED
                || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ConflictException("No se puede cancelar una reserva en estado: " + booking.getStatus());
        }

        updateBookingStatusAndNotify(booking, BookingStatus.CANCELED, comment, booking.getProvider(),
                "Reserva cancelada por el cliente", "El cliente ha cancelado la reserva");
    }

    private void handleRejected(Booking booking, Long authId, String comment) {
        validateProviderAction(booking, authId, BookingStatus.PENDING, "rechazar la reserva");
        updateBookingStatusAndNotify(booking, BookingStatus.REJECTED, comment, booking.getClient(),
                "Reserva rechazada", "El proveedor ha rechazado la reserva");
    }

    private void handleCompleted(Booking booking, Long authId, String comment, Double finalPrice) {
        validateProviderAction(booking, authId, BookingStatus.CONFIRMED, "marcar la reserva como completada");

        // Si el provider proporciona un precio final, actualizar el priceAtBooking
        if (finalPrice != null) {
            booking.setPriceAtBooking(finalPrice);
        }

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

        NotificationType type = switch (status) {
            case CONFIRMED ->
                    NotificationType.BOOKING_CONFIRMED;
            case CANCELED ->
                    NotificationType.BOOKING_CANCELLED;
            case REJECTED ->
                    NotificationType.BOOKING_CANCELLED;
            case COMPLETED ->
                    NotificationType.BOOKING_COMPLETED;
            default ->
                    NotificationType.SYSTEM;
        };

        notificationService.notifyUser(
                recipient,
                title,
                comment != null ? defaultMessage + ": " + comment : defaultMessage,
                type);
    }
}
