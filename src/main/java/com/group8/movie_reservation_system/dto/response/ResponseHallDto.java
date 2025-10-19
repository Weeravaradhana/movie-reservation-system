package com.group8.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseHallDto {
    private Long hallId;
    private String name;
    private int capacity;
    private String type;

    private List<ResponseSeatDto> seats;
}
