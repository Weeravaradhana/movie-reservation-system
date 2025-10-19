package com.group8.movie_reservation_system.dto.response.paginate;

import com.group8.movie_reservation_system.dto.response.ResponseTicketDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketPaginateResponseDto {
    private List<ResponseTicketDto> dataList;
    private long dataCount;
}
