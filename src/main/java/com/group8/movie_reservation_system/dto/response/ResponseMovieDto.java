package com.group8.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMovieDto {
    private Long movieId;
    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private String rating;
    private String trailerUrl;
    private String status;

    private List<ResponseShowtimeDto> showtime;
}
