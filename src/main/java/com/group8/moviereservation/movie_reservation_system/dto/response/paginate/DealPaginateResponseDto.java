package com.group8.moviereservation.movie_reservation_system.dto.response.paginate;

import com.group8.moviereservation.movie_reservation_system.dto.response.ResponseDealDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealPaginateResponseDto {
    private List<ResponseDealDto> dataList;
    private long dataCount;
}
