package com.group8.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseReviewDto {
    private Long id;
    private String content;
    private Integer rating;
    private String status;
    private String userId;
    private String userName;
    private String adminResponse;
    private LocalDateTime adminResponseAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
