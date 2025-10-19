package com.group8.movie_reservation_system.dto.response.paginate;
import com.group8.movie_reservation_system.dto.response.ResponseUserDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserPaginateResponseDto {
    private List<ResponseUserDto> dataList;
    private long dataCount;
}
