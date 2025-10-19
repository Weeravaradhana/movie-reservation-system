package com.group8.movie_reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBookingDto {
    private Long bookingId;
    private String status;
    private LocalDateTime timestamp;
    private ResponseShowtimeDto showtime;
    private List<ResponseSeatDto> seats;
    private ResponseUserDto user;
}
