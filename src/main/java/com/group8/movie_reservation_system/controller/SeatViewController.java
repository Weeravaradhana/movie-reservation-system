package com.group8.movie_reservation_system.controller;

import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseSeatDto;
import com.group8.movie_reservation_system.dto.response.ResponseShowtimeDto;
import com.group8.movie_reservation_system.entity.Seat;
import com.group8.movie_reservation_system.entity.Showtime;
import com.group8.movie_reservation_system.service.MovieService;
import com.group8.movie_reservation_system.service.SeatService;
import com.group8.movie_reservation_system.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class SeatViewController {

    private final SeatService seatService;
    private final ShowtimeService showtimeService;
    private final MovieService movieService; // ✅ add this


    @GetMapping("/movie/{movieId}/showtime/{showtimeId}/seats")
    public String selectSeats(@PathVariable Long movieId,
                              @PathVariable Long showtimeId,
                              Model model) {
        ResponseMovieDto movie = movieService.getMovieById(movieId);

        ResponseShowtimeDto showtime = showtimeService.getShowtimeById(showtimeId);

        List<Seat> seats = seatService.getSeatsByHallId(showtime.getHall().getHallId());

        List<ResponseSeatDto> seatDtos = seats.stream()
                .map(seat -> ResponseSeatDto.builder()
                        .seatNumber(seat.getSeatNumber())
                        .price(seat.getPrice())
                        .status(seat.getStatus())
                        .booked(seat.isBooked())
                        .seatId(seat.getSeatId())
                        .build())
                .collect(Collectors.toList());

        // 5. Send to Thymeleaf
        model.addAttribute("movie", movie);
        model.addAttribute("showtime", showtime);
        model.addAttribute("seats", seatDtos);

        return "booking/seats";
    }


}
