package com.group8.movie_reservation_system.dto.response.paginate;

import com.group8.movie_reservation_system.dto.response.ResponsePromoCodeDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromoCodePaginateResponseDto {
    private List<ResponsePromoCodeDto> dataList;
    private long dataCount;
}
