package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.booking.*;
import com.fran.jobsy.app.dto.offering.OfferingSummaryDTO;
import com.fran.jobsy.app.dto.user.UserSummaryDTO;
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
import com.fran.jobsy.app.service.NotificationService;
import com.fran.jobsy.app.service.ProviderAvailabilityService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final OfferingRepository offeringRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ProviderAvailabilityService providerAvailabilityService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDTO createBooking(BookingRequest bookingReq) {

        User provider = userRepository.findById(bookingReq.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));

        Offering offering = offeringRepository.findById(bookingReq.offeringId())
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrado."));

        User client = authenticatedUserProvider.getAuthenticatedUser();

        if (provider.getId().equals(client.getId())) {
            throw new ConflictException("No puedes reservar tus propios servicios");
        }

        AvailabilityCheckResponse check = providerAvailabilityService.checkAvailability(provider.getId(),
                bookingReq.startsAt(), bookingReq.endsAt());

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

        notificationService.notifyUser(client, "Reserva solicitada con éxito.", "Su reserva ha sido creada y está pendiente de confirmación.", NotificationType.BOOKING, true);

        return new BookingResponseDTO(
                booking.getId(),
                new UserSummaryDTO(
                        client.getId(),
                        client.getFirstname() + " " + client.getLastname(),
                        client.getPhoto().getUrl() != null ? client.getPhoto().getUrl() : "",
                        client.getCountry()),
                new UserSummaryDTO(
                        provider.getId(),
                        provider.getFirstname() + " " + provider.getLastname(),
                        provider.getPhoto().getUrl() != null ? provider.getPhoto().getUrl() : "",
                        provider.getCountry()),
                new OfferingSummaryDTO(
                        offering.getId(),
                        offering.getCategory().getName(),
                        offering.getTitle(),
                        offering.getBasePrice()),
                booking.getStartsAt(),
                booking.getEndsAt(),
                booking.getStatus(),
                booking.getPriceAtBooking(),
                booking.getAddressText(),
                booking.getLat(),
                booking.getLng(),
                null
        );
    }

    @Override
    public List<BookingListDTO> getClientBookings() {
        Long clientId = authenticatedUserProvider.getAuthenticatedUserId();
        List<Booking> bookings = bookingRepository.findAllByClientId(clientId);


        return bookings.stream()
                .map(bookingMapper::toBookingListDTO)
                .toList();
    }

    @Override
    public BookingResponseDTO getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        User client = booking.getClient();
        User provider = booking.getProvider();
        Offering offering = booking.getOffering();

        return new BookingResponseDTO(
                booking.getId(),
                new UserSummaryDTO(
                        client.getId(),
                        client.getFirstname() + " " + client.getLastname(),
                        client.getPhoto() != null && client.getPhoto().getUrl() != null ? client.getPhoto().getUrl() : "",
                        client.getCountry()),
                new UserSummaryDTO(
                        provider.getId(),
                        provider.getFirstname() + " " + provider.getLastname(),
                        provider.getPhoto() != null && provider.getPhoto().getUrl() != null ? provider.getPhoto().getUrl() : "",
                        provider.getCountry()),
                new OfferingSummaryDTO(
                        offering.getId(),
                        offering.getCategory().getName(),
                        offering.getTitle(),
                        offering.getBasePrice()),
                booking.getStartsAt(),
                booking.getEndsAt(),
                booking.getStatus(),
                booking.getPriceAtBooking(),
                booking.getAddressText(),
                booking.getLat(),
                booking.getLng(),
                booking.getStatusComment()
        );
    }

    @Override
    public BookingResponseDTO updateBookingStatus(Long id, BookingStatusUpdateReqDTO statusUpdateReqDTO) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        Long authId = authenticatedUserProvider.getAuthenticatedUserId();
        BookingStatus newStatus = statusUpdateReqDTO.status();
        String comment = statusUpdateReqDTO.comment();

        switch (newStatus) {
            case CONFIRMED ->
                    handleConfirmed(booking, authId, comment);
            case CANCELED ->
                    handleCanceled(booking, authId, comment);
            case COMPLETED ->
                    handleCompleted(booking, authId, comment);
            default ->
                    throw new ConflictException("Estado de reserva no válido: " + newStatus);
        }

        return getBooking(id);
    }

    // --- Private Update booking status helpers ---
    private void handleConfirmed(Booking booking, Long authId, String comment) {
        if (!booking.getProvider().getId().equals(authId)) {
            throw new AccessDeniedException("Solo el proveedor puede confirmar la reserva.");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("No se puede confirmar una reserva en estado: " + booking.getStatus());
        }

        updateBookingStatusAndNotify(booking, BookingStatus.CONFIRMED, comment, booking.getClient(),
                "Reserva confirmada", "La reserva fue confirmada");
    }

    private void handleCanceled(Booking booking, Long authId, String comment) {
        boolean isProvider = booking.getProvider().getId().equals(authId);
        boolean isClient = booking.getClient().getId().equals(authId);

        if (!isProvider && !isClient) {
            throw new AccessDeniedException("No autorizado para cancelar la reserva.");
        }
        if (booking.getStatus() == BookingStatus.CANCELED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ConflictException("No se puede cancelar una reserva en estado: " + booking.getStatus());
        }

        User recipient = isProvider ? booking.getClient() : booking.getProvider();
        updateBookingStatusAndNotify(booking, BookingStatus.CANCELED, comment, recipient,
                "Reserva cancelada", "La reserva fue cancelada");
    }

    private void handleCompleted(Booking booking, Long authId, String comment) {
        if (!booking.getProvider().getId().equals(authId)) {
            throw new AccessDeniedException("Solo el proveedor puede marcar la reserva como completada.");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("Solo una reserva confirmada puede marcarse como completada.");
        }

        updateBookingStatusAndNotify(booking, BookingStatus.COMPLETED, comment, booking.getClient(),
                "Reserva completada", "La reserva fue completada");
    }

    private void updateBookingStatusAndNotify(Booking booking, BookingStatus status, String comment,
                                              User recipient, String title, String defaultMessage) {
        booking.setStatus(status);
        booking.setStatusComment(comment);
        bookingRepository.save(booking);

        notificationService.notifyUser(recipient, title,
                comment != null ? defaultMessage + ": " + comment : defaultMessage,
                NotificationType.BOOKING, true);
    }
}
