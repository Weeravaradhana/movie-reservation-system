package com.group8.movie_reservation_system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestShowtimeDto {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @Min(value = 1, message = "Hall ID must be valid")
    private Long hallId;

    @NotBlank(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Time is required")
    private LocalTime time;


}
