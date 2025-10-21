package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestSeatSelectionDto;
import com.group8.movie_reservation_system.dto.response.ResponseHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseSeatDto;
import com.group8.movie_reservation_system.entity.Seat;
import com.group8.movie_reservation_system.service.SeatService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seat-management-service/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    // Get seat by ID
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/{seatId}")
    public ResponseEntity<StandardResponseDto> getSeat(@PathVariable Long seatId) {
        ResponseSeatDto seat = seatService.getSeatById(seatId);
        if(seat == null){
            return new ResponseEntity<>(new StandardResponseDto(404, "Seat not found", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat fetched successfully", seat), HttpStatus.OK);
    }

    // Get available seats for a showtime (booked=false only)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/available-seats/{showtimeId}")
    public ResponseEntity<StandardResponseDto> getAvailableSeatsByShowtime(@PathVariable Long showtimeId) {
        ResponseHallDto hall = seatService.getDefaultHallForShowtime(showtimeId);

        if(hall == null){
            return new ResponseEntity<>(new StandardResponseDto(404, "Showtime/Hall not found", null), HttpStatus.NOT_FOUND);
        }

        // booked=false seats filter
        List<ResponseSeatDto> availableSeats = hall.getSeats().stream()
                .filter(seat -> !seat.isBooked())
                .toList();

        hall.setSeats(availableSeats);

        return new ResponseEntity<>(new StandardResponseDto(200, "Available seats fetched", hall), HttpStatus.OK);
    }



    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/available-seats")
    public ResponseEntity<StandardResponseDto> getAvailableSeats() {


        return new ResponseEntity<>(new StandardResponseDto(200, "Available seats fetched", seatService.getAllSeats()), HttpStatus.OK);
    }

    @PostMapping("/api/confirm-seats/{showtimeId}")
    @ResponseBody
    public ResponseEntity<?> confirmSeats(
            @PathVariable Long showtimeId,
            @RequestBody List<RequestSeatSelectionDto> selections) {
        seatService.bookSeats(showtimeId, selections);
        return ResponseEntity.ok(Map.of("message", "Seats booked successfully"));
    }




    // Check if seat is booked
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/isBooked")
    public ResponseEntity<StandardResponseDto> isSeatBooked(@RequestParam Long seatId,
                                                            @RequestParam Long showtimeId) {
        boolean booked = seatService.isSeatBookedForShowtime(seatId, showtimeId);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat booking status fetched", booked), HttpStatus.OK);
    }

    // Admin endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/hall/{hallId}")
    public ResponseEntity<StandardResponseDto> getHall(@PathVariable Long hallId) {
        ResponseHallDto hall = seatService.getHallById(hallId);
        if(hall == null){
            return new ResponseEntity<>(new StandardResponseDto(404, "Hall not found", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new StandardResponseDto(200, "Hall fetched successfully", hall), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{seatId}/allocate/{showtimeId}")
    public ResponseEntity<StandardResponseDto> allocateSeat(@PathVariable Long seatId,
                                                            @PathVariable Long showtimeId,
                                                            @RequestParam String status) {
        seatService.allocateSeatForShowtime(seatId, showtimeId, status);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat allocated successfully", null), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{seatId}/status")
    public ResponseEntity<StandardResponseDto> updateSeatStatus(@PathVariable Long seatId,
                                                                @RequestParam String status) {
        ResponseSeatDto seat = seatService.updateSeatStatus(seatId, status);
        if(seat == null){
            return new ResponseEntity<>(new StandardResponseDto(404, "Seat not found", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat status updated", seat), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{seatId}")
    public ResponseEntity<StandardResponseDto> deleteSeat(@PathVariable Long seatId) {
        seatService.deleteSeat(seatId);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat deleted successfully", null), HttpStatus.OK);
    }

    // Get all seats for a showtime (includes booked)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<StandardResponseDto> getSeatsByShowtime(@PathVariable Long showtimeId) {
        List<Seat> seats = seatService.getSeatsByShowtime(showtimeId);
        if(seats == null || seats.isEmpty()){
            return new ResponseEntity<>(new StandardResponseDto(404, "No seats found for this showtime", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new StandardResponseDto(200, "Seats fetched successfully", seats), HttpStatus.OK);
    }
}
