package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestReviewDto;
import com.group8.movie_reservation_system.dto.response.ResponseReviewDto;
import com.group8.movie_reservation_system.dto.response.paginate.ReviewPaginateResponseDto;
import com.group8.movie_reservation_system.entity.Review;
import com.group8.movie_reservation_system.entity.User;



public interface ReviewService {

    void saveReview(RequestReviewDto dto);
    ResponseReviewDto findById(Long id);
    ReviewPaginateResponseDto findByUser(String userId, int page, int pageSize);
    ReviewPaginateResponseDto findByUserOrderByCreatedAtDesc(User user, int page, int pageSize);
    ReviewPaginateResponseDto findByStatus(Review.ReviewStatus status, int page, int pageSize);
    ReviewPaginateResponseDto findByStatusOrderByCreatedAtDesc(Review.ReviewStatus status, int page, int pageSize);
    ReviewPaginateResponseDto findAllOrderByCreatedAtDesc(int page, int pageSize);
    ResponseReviewDto approveReview(Long id, String adminResponse);
    ResponseReviewDto rejectReview(Long id, String adminResponse);
    ResponseReviewDto replyToReview(Long id, String adminResponse);
    long countByStatus(Review.ReviewStatus status);
    Double getAverageRating();
    void deleteReview(Long id);
}
