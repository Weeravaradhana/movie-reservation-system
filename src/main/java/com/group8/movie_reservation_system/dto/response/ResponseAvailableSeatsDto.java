package com.group8.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAvailableSeatsDto {
    private String seatNumber; // eg: "A1", "B5"
    private String status;     // eg: "AVAILABLE", "BOOKED"
}
