package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.response.ResponseHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseSeatDto;
import com.group8.movie_reservation_system.entity.Hall;
import com.group8.movie_reservation_system.entity.SeatAllocation;


public interface SeatService {
    void initializeSeats(Hall hall);
    void allocateSeatForShowtime(Long seatId, Long showtimeId, String status);
    boolean isSeatBookedForShowtime(Long seatId, Long showtimeId);
    ResponseSeatDto updateSeatStatus(Long seatId, String status);
    void deleteSeat(Long seatId);
    public SeatAllocation bookSeatWithDeal(Long seatId, Long showtimeId,int promoCode);
    ResponseHallDto getDefaultHallForShowtime(Long showtimeId);
    ResponseSeatDto getSeatById(Long seatId);
    ResponseHallDto getHallById(Long hallId);
}
