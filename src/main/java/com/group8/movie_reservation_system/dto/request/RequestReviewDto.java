package com.group8.movie_reservation_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestReviewDto {
    private String content;
    private Long movieId;
    private Integer rating;
    private String userId;
}
