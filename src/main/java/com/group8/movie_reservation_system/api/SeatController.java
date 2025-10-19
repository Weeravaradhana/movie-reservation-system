package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.response.ResponseHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseSeatDto;
import com.group8.movie_reservation_system.service.SeatService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seat-management-service/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/{seatId}")
    public ResponseEntity<StandardResponseDto> getSeat(@PathVariable Long seatId) {
        ResponseSeatDto seat = seatService.getSeatById(seatId);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat fetched successfully", seat), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/default-hall/{showtimeId}")
    public ResponseEntity<StandardResponseDto> getDefaultHall(@PathVariable Long showtimeId) {
        ResponseHallDto hall = seatService.getDefaultHallForShowtime(showtimeId);
        return new ResponseEntity<>(new StandardResponseDto(200, "Default hall fetched", hall), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/isBooked")
    public ResponseEntity<StandardResponseDto> isSeatBooked(@RequestParam Long seatId,
                                                            @RequestParam Long showtimeId) {
        boolean booked = seatService.isSeatBookedForShowtime(seatId, showtimeId);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat booking status fetched", booked), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/hall/{hallId}")
    public ResponseEntity<StandardResponseDto> getHall(@PathVariable Long hallId, HttpSession session) {


        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can update deals", null),
                    HttpStatus.FORBIDDEN
            );
        }

        ResponseHallDto hall = seatService.getHallById(hallId);
        return new ResponseEntity<>(new StandardResponseDto(200, "Hall fetched successfully", hall), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{seatId}/allocate/{showtimeId}")
    public ResponseEntity<StandardResponseDto> allocateSeat(@PathVariable Long seatId,
                                                            @PathVariable Long showtimeId,
                                                            @RequestParam String status,
                                                            HttpSession session) {


        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can update deals", null),
                    HttpStatus.FORBIDDEN
            );
        }

        seatService.allocateSeatForShowtime(seatId, showtimeId, status);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat allocated successfully", null), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{seatId}/status")
    public ResponseEntity<StandardResponseDto> updateSeatStatus(@PathVariable Long seatId,
                                                                @RequestParam String status,
                                                                HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can update deals", null),
                    HttpStatus.FORBIDDEN
            );
        }

        ResponseSeatDto seat = seatService.updateSeatStatus(seatId, status);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat status updated", seat), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{seatId}")
    public ResponseEntity<StandardResponseDto> deleteSeat(@PathVariable Long seatId,HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can update deals", null),
                    HttpStatus.FORBIDDEN
            );
        }

        seatService.deleteSeat(seatId);
        return new ResponseEntity<>(new StandardResponseDto(200, "Seat deleted successfully", null), HttpStatus.OK);
    }
}
