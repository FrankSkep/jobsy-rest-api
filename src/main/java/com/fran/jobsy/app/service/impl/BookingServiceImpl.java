package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.booking.AvailabilityCheckResponse;
import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponseDTO;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.Offering;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.BookingStatus;
import com.fran.jobsy.app.enums.NotificationType;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.BookingRepository;
import com.fran.jobsy.app.repository.OfferingRepository;
import com.fran.jobsy.app.repository.UserRepository;
import com.fran.jobsy.app.service.BookingService;
import com.fran.jobsy.app.service.NotificationService;
import com.fran.jobsy.app.service.ProviderAvailabilityService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public BookingListDTO createBooking(BookingRequest bookingReq) {

        User provider = userRepository.findById(bookingReq.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));

        Offering offering = offeringRepository.findById(bookingReq.offeringId())
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrado."));

        Long clientId = authenticatedUserProvider.getAuthenticatedUserId();
        User client = authenticatedUserProvider.getUserReference(clientId);

        if (provider.getId().equals(client.getId())) {
            throw new ConflictException("No puedes reservar tus propios servicios");
        }

        AvailabilityCheckResponse check = providerAvailabilityService.checkAvailability(provider.getId(),
                LocalDateTime.parse(bookingReq.startsAt()), LocalDateTime.parse(bookingReq.endsAt()));

        if (!check.available()) {
            throw new ConflictException(check.message());
        }

        Booking booking = Booking.builder()
                .client(client)
                .provider(provider)
                .offering(offering)
                .startsAt(LocalDateTime.parse(bookingReq.startsAt()))
                .endsAt(LocalDateTime.parse(bookingReq.endsAt()))
                .status(BookingStatus.PENDING)
                .priceAtBooking(bookingReq.priceAtBooking())
                .addressText(bookingReq.addressText())
                .lat(bookingReq.lat())
                .lng(bookingReq.lng())
                .build();

        bookingRepository.save(booking);

        notificationService.notifyUser(client, "Reserva solicitada con éxito.", "Su reserva ha sido creada y está pendiente de confirmación.", NotificationType.BOOKING, true);

        return new BookingListDTO(
                booking.getId(),
                provider.getFirstname() + " " + provider.getLastname(),
                client.getFirstname() + " " + client.getLastname(),
                offering.getTitle(),
                booking.getStartsAt(),
                booking.getEndsAt(),
                booking.getStatus(),
                booking.getPriceAtBooking()
        );
    }

    @Override
    public List<BookingListDTO> getBookings() {
        return List.of();
    }

    @Override
    public BookingResponseDTO getBooking(Long id) {
        return null;
    }

    @Override
    public void confirmBooking(Long id) {

    }

    @Override
    public void cancelBooking(Long id) {

    }

    @Override
    public void completeBooking(Long id) {

    }
}
