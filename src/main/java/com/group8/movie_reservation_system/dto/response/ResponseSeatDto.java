
package com.group8.movie_reservation_system.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSeatDto {
    private Long seatId;
    private String seatNumber;  // STRING
    private int rowNum;
    private int colNum;
    private String type;
    private String category;
    private String layout;
    private String status;
    private boolean booked;
    private BigDecimal price;
    private ResponseBookingDto booking;
}
