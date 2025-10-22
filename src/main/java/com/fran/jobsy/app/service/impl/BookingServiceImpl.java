package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.booking.AvailabilityCheckResponse;
import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.booking.BookingRequest;
import com.fran.jobsy.app.dto.booking.BookingResponseDTO;
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

        notificationService.notifyUser(client, "Reserva solicitada con éxito.", "Su reserva ha sido creada y está pendiente de confirmación.", NotificationType.BOOKING, true);
        return null;
//        if (provider.getId().equals(client.getId())) {
//            throw new ConflictException("No puedes reservar tus propios servicios");
//        }
//
//        AvailabilityCheckResponse check = providerAvailabilityService.checkAvailability(provider.getId(),
//                bookingReq.startsAt(), bookingReq.endsAt());
//
//        if (!check.available()) {
//            throw new ConflictException(check.message());
//        }
//
//        Booking booking = Booking.builder()
//                .client(client)
//                .provider(provider)
//                .offering(offering)
//                .startsAt(bookingReq.startsAt())
//                .endsAt(bookingReq.endsAt())
//                .status(BookingStatus.PENDING)
//                .priceAtBooking(bookingReq.priceAtBooking())
//                .addressText(bookingReq.addressText())
//                .lat(bookingReq.lat())
//                .lng(bookingReq.lng())
//                .build();
//
//        bookingRepository.save(booking);
//
//        notificationService.notifyUser(client, "Reserva solicitada con éxito.", "Su reserva ha sido creada y está pendiente de confirmación.", NotificationType.BOOKING, true);
//
//        return new BookingResponseDTO(
//                booking.getId(),
//                new UserSummaryDTO(
//                        client.getId(),
//                        client.getFirstname() + " " + client.getLastname(),
//                        client.getPhoto().getUrl() != null ? client.getPhoto().getUrl() : "",
//                        client.getCountry()),
//                new UserSummaryDTO(
//                        provider.getId(),
//                        provider.getFirstname() + " " + provider.getLastname(),
//                        provider.getPhoto().getUrl() != null ? provider.getPhoto().getUrl() : "",
//                        provider.getCountry()),
//                new OfferingSummaryDTO(
//                        offering.getId(),
//                        offering.getCategory().getName(),
//                        offering.getTitle(),
//                        offering.getBasePrice()),
//                booking.getStartsAt(),
//                booking.getEndsAt(),
//                booking.getStatus(),
//                booking.getPriceAtBooking(),
//                booking.getAddressText(),
//                booking.getLat(),
//                booking.getLng()
//        );
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
