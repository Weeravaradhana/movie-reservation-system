package com.group8.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSeatDto {
    private Long seatId;
    private String seatNumber;
    private String type;
    private String category;
    private String layout;
    private String status;
    private int rowNum;
    private int colNum;
    private BigDecimal price;
    private ResponseBookingDto booking;

}
