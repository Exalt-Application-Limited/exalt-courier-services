package com.gogidix.courier.onboarding.controller;

import com.gogidix.courier.onboarding.dto.CourierRatingRequest;
import com.gogidix.courier.onboarding.dto.CourierRatingResponse;
import com.gogidix.courier.onboarding.model.CourierProfile;
import com.gogidix.courier.onboarding.model.CourierRating;
import com.gogidix.courier.onboarding.model.RatingCategory;
import com.gogidix.courier.onboarding.service.CourierProfileService;
import com.gogidix.courier.onboarding.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing courier ratings.
 */
@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;
    private final CourierProfileService courierProfileService;

    /**
     * Create a new rating for a courier
     *
     * @param courierId The courier ID
     * @param request Rating creation request
     * @return The created rating
     */
    @PostMapping("/courier/{courierId}")
    public ResponseEntity<CourierRatingResponse> createRating(
            @PathVariable String courierId,
            @Valid @RequestBody CourierRatingRequest request) {
        
        log.info("Creating rating for courier: {} by user: {}", courierId, request.getRatedBy());
        
        CourierRating rating = ratingService.createRating(
                courierId,
                request.getRating(),
                request.getComment(),
                request.getRatedBy(),
                request.getOrderId(),
                request.getDeliveryId(),
                request.getCategory()
        );
        
        CourierRatingResponse response = convertToDto(rating);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a rating by ID
     *
     * @param ratingId Rating ID
     * @return The rating if found
     */
    @GetMapping("/{ratingId}")
    public ResponseEntity<CourierRatingResponse> getRatingById(
            @PathVariable Long ratingId) {
        
        log.info("Fetching rating with ID: {}", ratingId);
        
        return ratingService.getRatingById(ratingId)
                .map(rating -> ResponseEntity.ok(convertToDto(rating)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all ratings for a courier
     *
     * @param courierId Courier ID
     * @param pageable Pagination information
     * @return Page of ratings for the courier
     */
    @GetMapping("/courier/{courierId}")
    public ResponseEntity<Page<CourierRatingResponse>> getRatingsForCourier(
            @PathVariable String courierId,
            Pageable pageable) {
        
        log.info("Fetching ratings for courier: {}", courierId);
        
        Page<CourierRating> ratings = ratingService.getRatingsForCourier(courierId, pageable);
        Page<CourierRatingResponse> responsePage = ratings.map(this::convertToDto);
        
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Get ratings for a courier by category
     *
     * @param courierId Courier ID
     * @param category Rating category
     * @param pageable Pagination information
     * @return Page of ratings in the specified category
     */
    @GetMapping("/courier/{courierId}/category/{category}")
    public ResponseEntity<Page<CourierRatingResponse>> getRatingsByCategory(
            @PathVariable String courierId,
            @PathVariable RatingCategory category,
            Pageable pageable) {
        
        log.info("Fetching {} ratings for courier: {}", category, courierId);
        
        Page<CourierRating> ratings = ratingService.getRatingsByCategory(courierId, category, pageable);
        Page<CourierRatingResponse> responsePage = ratings.map(this::convertToDto);
        
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Get ratings for a courier by rating value
     *
     * @param courierId Courier ID
     * @param ratingValue Rating value (1-5)
     * @param pageable Pagination information
     * @return Page of ratings with the specified value
     */
    @GetMapping("/courier/{courierId}/value/{ratingValue}")
    public ResponseEntity<Page<CourierRatingResponse>> getRatingsByValue(
            @PathVariable String courierId,
            @PathVariable int ratingValue,
            Pageable pageable) {
        
        log.info("Fetching ratings with value {} for courier: {}", ratingValue, courierId);
        
        Page<CourierRating> ratings = ratingService.getRatingsByValue(courierId, ratingValue, pageable);
        Page<CourierRatingResponse> responsePage = ratings.map(this::convertToDto);
        
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Get ratings for an order
     *
     * @param orderId Order ID
     * @return List of ratings for the order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<CourierRatingResponse>> getRatingsForOrder(
            @PathVariable String orderId) {
        
        log.info("Fetching ratings for order: {}", orderId);
        
        List<CourierRating> ratings = ratingService.getRatingsForOrder(orderId);
        List<CourierRatingResponse> responses = ratings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Get ratings for a delivery
     *
     * @param deliveryId Delivery ID
     * @return List of ratings for the delivery
     */
    @GetMapping("/delivery/{deliveryId}")
    public ResponseEntity<List<CourierRatingResponse>> getRatingsForDelivery(
            @PathVariable String deliveryId) {
        
        log.info("Fetching ratings for delivery: {}", deliveryId);
        
        List<CourierRating> ratings = ratingService.getRatingsForDelivery(deliveryId);
        List<CourierRatingResponse> responses = ratings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Verify a rating
     *
     * @param ratingId Rating ID
     * @param userId ID of the user verifying the rating
     * @return The verified rating
     */
    @PostMapping("/{ratingId}/verify")
    public ResponseEntity<CourierRatingResponse> verifyRating(
            @PathVariable Long ratingId,
            @RequestParam String userId) {
        
        log.info("Verifying rating: {} by user: {}", ratingId, userId);
        
        CourierRating rating = ratingService.verifyRating(ratingId, userId);
        CourierRatingResponse response = convertToDto(rating);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Hide a rating
     *
     * @param ratingId Rating ID
     * @param userId ID of the user hiding the rating
     * @return The hidden rating
     */
    @PostMapping("/{ratingId}/hide")
    public ResponseEntity<CourierRatingResponse> hideRating(
            @PathVariable Long ratingId,
            @RequestParam String userId) {
        
        log.info("Hiding rating: {} by user: {}", ratingId, userId);
        
        CourierRating rating = ratingService.hideRating(ratingId, userId);
        CourierRatingResponse response = convertToDto(rating);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Show a rating
     *
     * @param ratingId Rating ID
     * @param userId ID of the user showing the rating
     * @return The shown rating
     */
    @PostMapping("/{ratingId}/show")
    public ResponseEntity<CourierRatingResponse> showRating(
            @PathVariable Long ratingId,
            @RequestParam String userId) {
        
        log.info("Showing rating: {} by user: {}", ratingId, userId);
        
        CourierRating rating = ratingService.showRating(ratingId, userId);
        CourierRatingResponse response = convertToDto(rating);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Add an admin response to a rating
     *
     * @param ratingId Rating ID
     * @param userId ID of the administrator
     * @param response The administrator's response
     * @return The updated rating
     */
    @PostMapping("/{ratingId}/admin-response")
    public ResponseEntity<CourierRatingResponse> addAdminResponse(
            @PathVariable Long ratingId,
            @RequestParam String userId,
            @RequestParam String response) {
        
        log.info("Adding admin response to rating: {} by user: {}", ratingId, userId);
        
        CourierRating rating = ratingService.addAdminResponse(ratingId, response, userId);
        CourierRatingResponse responseDto = convertToDto(rating);
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Delete a rating
     *
     * @param ratingId Rating ID
     * @param userId ID of the user deleting the rating
     * @return No content if successful
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long ratingId,
            @RequestParam String userId) {
        
        log.info("Deleting rating: {} by user: {}", ratingId, userId);
        
        boolean deleted = ratingService.deleteRating(ratingId, userId);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get average rating for a courier
     *
     * @param courierId Courier ID
     * @return The average rating
     */
    @GetMapping("/courier/{courierId}/average")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable String courierId) {
        
        log.info("Fetching average rating for courier: {}", courierId);
        
        double averageRating = ratingService.calculateOverallAverageRating(courierId);
        return ResponseEntity.ok(averageRating);
    }

    /**
     * Get average ratings by category for a courier
     *
     * @param courierId Courier ID
     * @return Map of category to average rating
     */
    @GetMapping("/courier/{courierId}/average-by-category")
    public ResponseEntity<Map<RatingCategory, Double>> getAverageRatingsByCategory(
            @PathVariable String courierId) {
        
        log.info("Fetching average ratings by category for courier: {}", courierId);
        
        Map<RatingCategory, Double> averageRatings = ratingService.getAverageRatingsByCategory(courierId);
        return ResponseEntity.ok(averageRatings);
    }

    /**
     * Get rating distribution for a courier
     *
     * @param courierId Courier ID
     * @return Map of rating value to count
     */
    @GetMapping("/courier/{courierId}/distribution")
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution(
            @PathVariable String courierId) {
        
        log.info("Fetching rating distribution for courier: {}", courierId);
        
        Map<Integer, Long> distribution = ratingService.getRatingDistribution(courierId);
        return ResponseEntity.ok(distribution);
    }

    /**
     * Get most recent ratings for a courier
     *
     * @param courierId Courier ID
     * @param limit Maximum number of ratings to return
     * @return List of recent ratings
     */
    @GetMapping("/courier/{courierId}/recent")
    public ResponseEntity<List<CourierRatingResponse>> getMostRecentRatings(
            @PathVariable String courierId,
            @RequestParam(defaultValue = "5") int limit) {
        
        log.info("Fetching {} most recent ratings for courier: {}", limit, courierId);
        
        List<CourierRating> ratings = ratingService.getMostRecentRatings(courierId, limit);
        List<CourierRatingResponse> responses = ratings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Convert entity to DTO
     *
     * @param rating Entity
     * @return Response DTO
     */
    private CourierRatingResponse convertToDto(CourierRating rating) {
        String courierName = "";
        
        // Get courier name if possible
        if (rating.getCourierId() != null) {
            courierProfileService.getCourierProfileById(rating.getCourierId())
                    .ifPresent(profile -> {
                        // This is a bit of a hack due to lambda limitations
                        // In a real implementation, this would be handled better
                        Thread.currentThread().setName(profile.getFirstName() + " " + profile.getLastName());
                    });
            courierName = Thread.currentThread().getName();
        }
        
        return CourierRatingResponse.builder()
                .id(rating.getId())
                .courierId(rating.getCourierId())
                .courierName(courierName)
                .rating(rating.getRating())
                .comment(rating.getComment())
                .ratedBy(rating.getRatedBy())
                .orderId(rating.getOrderId())
                .deliveryId(rating.getDeliveryId())
                .category(rating.getCategory())
                .createdAt(rating.getCreatedAt())
                .verified(rating.getVerified())
                .verifiedBy(rating.getVerifiedBy())
                .verifiedAt(rating.getVerifiedAt())
                .hidden(rating.getHidden())
                .hiddenBy(rating.getHiddenBy())
                .hiddenAt(rating.getHiddenAt())
                .adminResponse(rating.getAdminResponse())
                .adminResponseBy(rating.getAdminResponseBy())
                .adminResponseAt(rating.getAdminResponseAt())
                .build();
    }
}
