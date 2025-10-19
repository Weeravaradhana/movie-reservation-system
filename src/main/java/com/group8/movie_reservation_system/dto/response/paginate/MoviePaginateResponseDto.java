package com.group8.movie_reservation_system.dto.response.paginate;

import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoviePaginateResponseDto {
    private List<ResponseMovieDto> dataList;
    private long dataCount;
}
