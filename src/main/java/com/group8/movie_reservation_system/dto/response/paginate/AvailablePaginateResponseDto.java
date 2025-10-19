package com.group8.movie_reservation_system.dto.response.paginate;

import com.group8.movie_reservation_system.dto.response.ResponseAvailableSeatsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailablePaginateResponseDto {
    private List<ResponseAvailableSeatsDto> dataList;  // Paginated or full seat list
    private long dataCount;                            // Total seat count
}
