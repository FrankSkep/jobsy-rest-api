package com.fran.jobsy.app.service.review;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.review.ReviewRequest;
import com.fran.jobsy.app.dto.review.ReviewResponse;
import com.fran.jobsy.app.dto.review.ReviewSummaryResponse;
import com.fran.jobsy.app.entity.Booking;
import com.fran.jobsy.app.entity.Review;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.BookingStatus;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.exception.custom.ConflictException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.exception.custom.UnauthorizedAccessException;
import com.fran.jobsy.app.mapper.ReviewMapper;
import com.fran.jobsy.app.repository.BookingRepository;
import com.fran.jobsy.app.repository.ReviewRepository;
import com.fran.jobsy.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse createReview(Long bookingId, ReviewRequest reviewRequest) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada. ID: " + bookingId));

        // validate rules to create review (status, uniqueness, authorization)
        validateCreateReview(booking);

        Review review = Review.builder()
                .booking(booking)
                .client(booking.getClient())
                .provider(booking.getProvider())
                .rating(reviewRequest.rating())
                .comment(reviewRequest.comment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // update provider average rating cache
        updateProviderAvgRating(booking.getProvider());

        return reviewMapper.toReviewDTO(savedReview);
    }

    @Override
    public List<ReviewSummaryResponse> getOfferingReviews(Long offeringId) {
        return reviewRepository.findByBooking_OfferingId(offeringId);
    }

    @Override
    public List<ReviewSummaryResponse> getProviderReviews(Long providerId) {
        User user = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado. ID: " + providerId));

        if (!user.getRole().equals(Role.PROVIDER)) {
            throw new UnauthorizedAccessException("El usuario no es un proveedor. ID: " + providerId);
        }

        return reviewRepository.findByProviderId(providerId);
    }

    // ----- Private Methods -----
    private void validateCreateReview(Booking booking) {
        // booking must be completed
        if (!BookingStatus.COMPLETED.equals(booking.getStatus())) {
            throw new ConflictException("No se puede crear una reseña para una reserva que no ha finalizado.");
        }

        // only one review per booking
        if (booking.getReview() != null) {
            throw new ConflictException("Ya hiciste una reseña para esta reserva.");
        }

        // only the client who made the booking can create the review
        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();
        Long clientId = booking.getClient().getId();
        if (!authenticatedUserId.equals(clientId)) {
            throw new UnauthorizedAccessException("No autorizado para crear una reseña en nombre de otro usuario.");
        }
    }

    private void updateProviderAvgRating(User provider) {
        Double avg = reviewRepository.calculateAverageRatingByProvider(provider.getId());
        provider.setAvgRatingCache(avg != null ? avg : 0.0);
        userRepository.save(provider);
    }
}
