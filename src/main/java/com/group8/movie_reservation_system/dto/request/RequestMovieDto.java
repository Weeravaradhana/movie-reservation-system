package com.group8.movie_reservation_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestMovieDto {

    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private double rating;
    private String posterUrl;
    private String status;
}
