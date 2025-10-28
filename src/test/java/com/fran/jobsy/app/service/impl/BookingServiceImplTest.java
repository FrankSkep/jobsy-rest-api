package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.booking.*;
import com.fran.jobsy.app.dto.offering.OfferingSummaryResponse;
import com.fran.jobsy.app.dto.user.UserSummaryResponse;
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
import com.fran.jobsy.app.service.booking.BookingServiceImpl;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private ProviderAvailabilityService providerAvailabilityService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ConversationService conversationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User client;
    private User provider;
    private Offering offering;
    private Booking booking;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private BookingSummaryResponse bookingSummaryResponse;
    private UserSummaryResponse clientSummary;
    private UserSummaryResponse providerSummary;
    private OfferingSummaryResponse offeringSummary;

    @BeforeEach
    void setUp() {
        client = User.builder().id(1L).build();
        provider = User.builder().id(2L).build();
        offering = Offering.builder().id(1L).build();
        booking = Booking.builder()
                .id(1L)
                .client(client)
                .provider(provider)
                .offering(offering)
                .status(BookingStatus.PENDING)
                .build();
        bookingRequest = new BookingRequest(2L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 100.0, "Address", 0.0, 0.0);
        clientSummary = new UserSummaryResponse(1L, "Client Name", "client.jpg", "Country");
        providerSummary = new UserSummaryResponse(2L, "Provider Name", "provider.jpg", "Country");
        offeringSummary = new OfferingSummaryResponse(1L, "Category", "Offering Title", 100.0);
        bookingResponse = new BookingResponse(1L, clientSummary, providerSummary, offeringSummary, LocalDateTime.now(), LocalDateTime.now().plusHours(1), BookingStatus.PENDING, 100.0, "Address", 0.0, 0.0, null);
        bookingSummaryResponse = new BookingSummaryResponse(1L, "Provider Name", "Client Name", "Offering Title", LocalDateTime.now(), LocalDateTime.now().plusHours(1), BookingStatus.PENDING, 100.0);
    }

    @Test
    void createBooking_ShouldCreateBooking_WhenValidRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(provider));
        when(offeringRepository.findById(1L)).thenReturn(Optional.of(offering));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(client);
        when(providerAvailabilityService.checkAvailability(anyLong(), any(), any())).thenReturn(new AvailabilityCheckResponse(true, "Available"));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDTO(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.createBooking(bookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
        verify(notificationService).notifyUser(eq(client), anyString(), anyString(), eq(NotificationType.BOOKING), eq(true));
    }

    @Test
    void createBooking_ShouldThrowConflictException_WhenClientIsProvider() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(provider));
        when(offeringRepository.findById(1L)).thenReturn(Optional.of(offering));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(provider);

        assertThrows(ConflictException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void createBooking_ShouldThrowConflictException_WhenNotAvailable() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(provider));
        when(offeringRepository.findById(1L)).thenReturn(Optional.of(offering));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(client);
        when(providerAvailabilityService.checkAvailability(anyLong(), any(), any())).thenReturn(new AvailabilityCheckResponse(false, "Not available"));

        assertThrows(ConflictException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void getClientBookings_ShouldReturnList() {
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(1L);
        when(bookingRepository.findAllByClientId(1L)).thenReturn(List.of(booking));
        when(bookingMapper.toSummaryDTO(booking)).thenReturn(bookingSummaryResponse);

        List<BookingSummaryResponse> result = bookingService.getClientBookings();

        assertEquals(1, result.size());
    }

    @Test
    void getProviderBookings_ShouldReturnList() {
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(2L);
        when(bookingRepository.findAllByProviderId(2L)).thenReturn(List.of(booking));
        when(bookingMapper.toSummaryDTO(booking)).thenReturn(bookingSummaryResponse);

        List<BookingSummaryResponse> result = bookingService.getProviderBookings();

        assertEquals(1, result.size());
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenExists() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.getBooking(1L);

        assertNotNull(result);
    }

    @Test
    void getBooking_ShouldThrowResourceNotFoundException_WhenNotExists() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBooking(1L));
    }

    @Test
    void updateBookingStatus_ShouldConfirmBooking_WhenProvider() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(2L);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDTO(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.updateBookingStatus(1L, new BookingStatusUpdateRequest(BookingStatus.CONFIRMED, "Confirmed"));

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        verify(conversationService).createIfNotExists(booking);
        verify(notificationService).notifyUser(eq(client), anyString(), anyString(), eq(NotificationType.BOOKING), eq(true));
    }

    @Test
    void updateBookingStatus_ShouldCancelBooking_WhenClient() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(1L);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDTO(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.updateBookingStatus(1L, new BookingStatusUpdateRequest(BookingStatus.CANCELED, "Canceled"));

        assertEquals(BookingStatus.CANCELED, booking.getStatus());
        verify(notificationService).notifyUser(eq(provider), anyString(), anyString(), eq(NotificationType.BOOKING), eq(true));
    }

    @Test
    void updateBookingStatus_ShouldCompleteBooking_WhenProvider() {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(2L);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDTO(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.updateBookingStatus(1L, new BookingStatusUpdateRequest(BookingStatus.COMPLETED, "Completed"));

        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        verify(notificationService).notifyUser(eq(client), anyString(), anyString(), eq(NotificationType.BOOKING), eq(true));
    }

    @Test
    void updateBookingStatus_ShouldThrowAccessDeniedException_WhenUnauthorized() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(3L);

        assertThrows(AccessDeniedException.class, () -> bookingService.updateBookingStatus(1L, new BookingStatusUpdateRequest(BookingStatus.CONFIRMED, "Confirmed")));
    }

    @Test
    void updateBookingStatus_ShouldThrowConflictException_WhenInvalidStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(2L);

        assertThrows(ConflictException.class, () -> bookingService.updateBookingStatus(1L, new BookingStatusUpdateRequest(BookingStatus.PENDING, "Invalid")));
    }
}
