package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestSeatSelectionDto;
import com.group8.movie_reservation_system.dto.response.ResponseHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseSeatDto;
import com.group8.movie_reservation_system.entity.Hall;
import com.group8.movie_reservation_system.entity.Seat;
import com.group8.movie_reservation_system.entity.SeatAllocation;

import java.math.BigDecimal;
import java.util.List;


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
    List<Seat> getSeatsByShowtime(Long showtimeId);
    BigDecimal bookSeats(Long showtimeId, List<RequestSeatSelectionDto> selectedSeats);
    ResponseHallDto getDefaultHall(Long showtimeId);
    public ResponseMovieDto getMovieById(Long movieId);
    public List<Seat> getSeatsByHallId(Long hallId);
    List<ResponseSeatDto> getAllSeats();
}