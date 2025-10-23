package com.fran.jobsy.app.service.impl;

import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.review.ReviewDTO;
import com.fran.jobsy.app.dto.review.ReviewRequest;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.Review;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.BookingStatus;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.repository.BookingRepository;
import com.fran.jobsy.app.repository.ReviewRepository;
import com.fran.jobsy.app.service.ReviewService;
import com.fran.jobsy.app.util.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public ReviewDTO createReview(Long bookingId, ReviewRequest reviewRequest) {
        Booking booking = findBookingOrThrow(bookingId);

        validateBookingStatus(booking);
        validateNoExistingReview(booking);
        validateReviewAuthorization(booking);
        validateRequestConsistency(booking, reviewRequest);

        Review savedReview = saveReview(booking, reviewRequest);

        return mapToReviewDTO(savedReview, booking);
    }

    @Override
    public List<ReviewDTO> getOfferingReviews(Long offeringId) {
        return reviewRepository.findByBooking_OfferingId(offeringId);
    }

    @Override
    public List<ReviewDTO> getProviderReviews(Long providerId) {
        return reviewRepository.findByProviderId(providerId);
    }

    // ---------- Private Methods ---------- //

    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada. ID: " + bookingId));
    }

    private void validateBookingStatus(Booking booking) {
        if (!BookingStatus.COMPLETED.equals(booking.getStatus())) {
            throw new ConflictException("No se puede crear una reseña para una reserva que no ha finalizado.");
        }
    }

    private void validateNoExistingReview(Booking booking) {
        if (booking.getReview() != null) {
            throw new ConflictException("Ya hiciste una reseña para esta reserva.");
        }
    }

    private void validateReviewAuthorization(Booking booking) {
        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();
        Long clientId = booking.getClient().getId();

        if (!authenticatedUserId.equals(clientId)) {
            throw new ConflictException("No autorizado para crear una reseña en nombre de otro usuario.");
        }
    }

    private void validateRequestConsistency(Booking booking, ReviewRequest reviewRequest) {
        User provider = booking.getProvider();
        User client = booking.getClient();

        if (!provider.getId().equals(reviewRequest.providerId())) {
            throw new ConflictException("Datos del proveedor no coinciden con la reserva.");
        }

        if (!client.getId().equals(reviewRequest.clientId())) {
            throw new ConflictException("Datos del cliente no coinciden con la reserva.");
        }
    }

    private Review saveReview(Booking booking, ReviewRequest reviewRequest) {
        Review review = Review.builder()
                .booking(booking)
                .client(booking.getClient())
                .provider(booking.getProvider())
                .rating(reviewRequest.rating())
                .comment(reviewRequest.comment())
                .build();

        return reviewRepository.save(review);
    }

    private ReviewDTO mapToReviewDTO(Review review, Booking booking) {
        return new ReviewDTO(
                review.getId(),
                new BookingListDTO(
                        booking.getId(),
                        booking.getProvider().getFirstname(),
                        booking.getClient().getFirstname(),
                        booking.getOffering().getTitle(),
                        booking.getStartsAt(),
                        booking.getEndsAt(),
                        booking.getStatus(),
                        booking.getPriceAtBooking()
                ),
                booking.getClient().getId(),
                booking.getProvider().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}