package com.group8.movie_reservation_system.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAvailableSeatsDto {
    private String seatNumber;   // STRING
    private int rowNum;
    private int colNum;
    private String status;
    private ResponseSeatDto seatDto;

    public ResponseAvailableSeatsDto(String seatNumber, String status) {
        this.seatNumber = seatNumber;
        this.status = status;
    }
}
