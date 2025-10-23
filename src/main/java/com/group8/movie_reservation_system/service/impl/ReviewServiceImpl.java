package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestReviewDto;
import com.group8.movie_reservation_system.dto.response.ResponseReviewDto;
import com.group8.movie_reservation_system.dto.response.paginate.ReviewPaginateResponseDto;
import com.group8.movie_reservation_system.entity.Movie;
import com.group8.movie_reservation_system.entity.Review;
import com.group8.movie_reservation_system.entity.User;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.MovieRepository;
import com.group8.movie_reservation_system.repo.ReviewRepository;
import com.group8.movie_reservation_system.repo.UserRepository;
import com.group8.movie_reservation_system.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Override
    public void saveReview(RequestReviewDto dto,String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("User not found"));
        Movie movie = movieRepository.findById(dto.getMovieId()).orElseThrow(() -> new EntryNotFoundException("Movie not found"));

        Review review = new Review(dto.getContent(), dto.getRating(), user,movie);
        reviewRepository.save(review);
    }

    @Override
    public ResponseReviewDto findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Review not found"));
        return toResponseReviewDto(review);
    }

    @Override
    public ReviewPaginateResponseDto findByUser(String userId, int page, int pageSize) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntryNotFoundException("User not found"));
        List<Review> reviews = reviewRepository.findByUser(user);
        return paginateList(reviews, page, pageSize);
    }

    @Override
    public ReviewPaginateResponseDto findByUserOrderByCreatedAtDesc(User user, int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, pageSize))
                .getContent();
        return ReviewPaginateResponseDto.builder()
                .dataList(reviews.stream().map(this::toResponseReviewDto).collect(Collectors.toList()))
                .dataCount(reviews.size())
                .build();
    }

    @Override
    public ReviewPaginateResponseDto findByStatus(Review.ReviewStatus status, int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByStatus(status);
        return paginateList(reviews, page, pageSize);
    }

    @Override
    public ReviewPaginateResponseDto findByStatusOrderByCreatedAtDesc(Review.ReviewStatus status, int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByStatusOrderByCreatedAtDesc(status);
        return paginateList(reviews, page, pageSize);
    }

    @Override
    public ReviewPaginateResponseDto findAllOrderByCreatedAtDesc(int page, int pageSize) {
        List<Review> reviews = reviewRepository.findAllOrderByCreatedAtDesc(PageRequest.of(page, pageSize)).getContent();
        return ReviewPaginateResponseDto.builder()
                .dataList(reviews.stream().map(this::toResponseReviewDto).collect(Collectors.toList()))
                .dataCount(reviews.size())
                .build();
    }

    @Override
    public ResponseReviewDto approveReview(Long id, String adminResponse) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Review not found"));
        review.setStatus(Review.ReviewStatus.APPROVED);
        review.setAdminResponse(adminResponse);
        review.setAdminResponseAt(LocalDateTime.now());
        return toResponseReviewDto(reviewRepository.save(review));
    }

    @Override
    public ResponseReviewDto rejectReview(Long id, String adminResponse) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Review not found"));
        review.setStatus(Review.ReviewStatus.REJECTED);
        review.setAdminResponse(adminResponse);
        review.setAdminResponseAt(LocalDateTime.now());
        return toResponseReviewDto(reviewRepository.save(review));
    }

    @Override
    public ResponseReviewDto replyToReview(Long id, String adminResponse) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Review not found"));
        review.setAdminResponse(adminResponse);
        review.setAdminResponseAt(LocalDateTime.now());
        return toResponseReviewDto(reviewRepository.save(review));
    }

    @Override
    public long countByStatus(Review.ReviewStatus status) {
        return reviewRepository.countByStatus(status);
    }

    @Override
    public Double getAverageRating() {
        return reviewRepository.getAverageRating();
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    private ResponseReviewDto toResponseReviewDto(Review review) {
        return ResponseReviewDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .status(review.getStatus().name())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .userName(review.getUser() != null ? review.getUser().getFullName() : null)
                .adminResponse(review.getAdminResponse())
                .adminResponseAt(review.getAdminResponseAt())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private ReviewPaginateResponseDto paginateList(List<Review> reviews, int page, int pageSize) {
        int total = reviews.size();
        int fromIndex = Math.min(page * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<ResponseReviewDto> pagedReviews = reviews.subList(fromIndex, toIndex)
                .stream()
                .map(this::toResponseReviewDto)
                .collect(Collectors.toList());
        return ReviewPaginateResponseDto.builder()
                .dataList(pagedReviews)
                .dataCount(total)
                .build();
    }
}
