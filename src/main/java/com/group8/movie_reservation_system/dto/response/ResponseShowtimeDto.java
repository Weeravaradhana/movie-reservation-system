package com.group8.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseShowtimeDto {
    private Long showtimeId;
    private LocalDate date;
    private String title;
    private LocalTime time;
    private BigDecimal price;
    private ResponseMovieDto movie;
    private ResponseHallDto hall;
}
