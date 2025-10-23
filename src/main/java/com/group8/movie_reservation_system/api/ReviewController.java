package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestReviewDto;
import com.group8.movie_reservation_system.dto.response.ResponseReviewDto;
import com.group8.movie_reservation_system.entity.Review;
import com.group8.movie_reservation_system.service.ReviewService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review-management-service/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> createReview(@RequestBody RequestReviewDto dto, HttpSession session) {
        System.out.println("djfjdhfjdfhjdh" + dto.getMovieId());
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");

        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {
            reviewService.saveReview(dto,loggedUserId);
            return ResponseEntity.ok(
                    StandardResponseDto.builder()
                            .code(200)
                            .message("Review submitted successfully")
                            .data(null)
                            .build()
            );
        }

        return new ResponseEntity<>(
                new StandardResponseDto(401, "Unauthorized: No active session", null),
                HttpStatus.UNAUTHORIZED
        );

    }


    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> approveReview(
            @PathVariable Long id,
            @RequestParam String adminResponse,
            HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");

        if (loggedUserRole.equals("ROLE_ADMIN")) {
            ResponseReviewDto response = reviewService.approveReview(id, adminResponse);
            return ResponseEntity.ok(
                    StandardResponseDto.builder()
                            .code(200)
                            .message("Review approved successfully")
                            .data(response)
                            .build()
            );
        }
        else{
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }

    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> rejectReview(
            @PathVariable Long id,
            @RequestParam String adminResponse,
            HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");

        if (loggedUserRole.equals("ROLE_ADMIN")) {
            ResponseReviewDto response = reviewService.rejectReview(id, adminResponse);
            return ResponseEntity.ok(
                    StandardResponseDto.builder()
                            .code(200)
                            .message("Review rejected successfully")
                            .data(response)
                            .build()
            );
        }
        else{
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }


    }

    @PutMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<StandardResponseDto> replyToReview(
            @PathVariable Long id,
            @RequestParam String adminResponse,
            HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("USER")) {
            ResponseReviewDto response = reviewService.replyToReview(id, adminResponse);
            return ResponseEntity.ok(
                    StandardResponseDto.builder()
                            .code(200)
                            .message("Reply submitted successfully")
                            .data(response)
                            .build()
            );
        }

        return new ResponseEntity<>(
                new StandardResponseDto(401, "Unauthorized: No active session", null),
                HttpStatus.UNAUTHORIZED
        );




    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> getReviewsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {


        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {
            return ResponseEntity.ok(
                    StandardResponseDto.builder()
                            .code(200)
                            .message("User reviews fetched successfully")
                            .data(reviewService.findByUser(userId, page, pageSize))
                            .build()
            );
        }

        return new ResponseEntity<>(
                new StandardResponseDto(401, "Unauthorized: No active session", null),
                HttpStatus.UNAUTHORIZED
        );




    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> getReviewsByStatus(
            @PathVariable Review.ReviewStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(
                    StandardResponseDto.builder()
                            .code(200)
                            .message("Reviews by status fetched successfully")
                            .data( reviewService.findByStatus(status, page, pageSize))
                            .build()
            );
        }
        else{
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }



    }


    @GetMapping("/admin/find-all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> getAllReviews(
            HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if ("ROLE_ADMIN".equals(loggedUserRole)) {
            return ResponseEntity.ok(
                    StandardResponseDto.builder()
                            .code(200)
                            .message("All reviews fetched successfully")
                            .data(reviewService.findAll())
                            .build()
            );
        }

        return new ResponseEntity<>(
                new StandardResponseDto(403, "Forbidden: Only admin can view all reviews", null),
                HttpStatus.FORBIDDEN
        );
    }

}
