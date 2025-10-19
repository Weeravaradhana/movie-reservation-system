package com.group8.movie_reservation_system.dto.response.paginate;

import com.group8.movie_reservation_system.dto.response.ResponseReviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPaginateResponseDto {
    private List<ResponseReviewDto> dataList; // Paginated reviews
    private long dataCount;                    // Total number of reviews
}
