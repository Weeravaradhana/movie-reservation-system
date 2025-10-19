package com.group8.movie_reservation_system.dto.response.paginate;

import com.group8.movie_reservation_system.dto.response.ResponseBookingDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingPaginateResponseDto {
    private List<ResponseBookingDto> dataList;
    private long dataCount;
}
