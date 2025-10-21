package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestBookingDto;
import com.group8.movie_reservation_system.dto.request.RequestSeatSelectionDto;
import com.group8.movie_reservation_system.dto.response.ResponseBookingDto;
import com.group8.movie_reservation_system.dto.response.paginate.AvailablePaginateResponseDto;
import com.group8.movie_reservation_system.dto.response.paginate.BookingPaginateResponseDto;
import com.group8.movie_reservation_system.exception.DuplicateEntryException;
import com.group8.movie_reservation_system.service.BookingService;
import com.group8.movie_reservation_system.service.SeatService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/booking-management-service/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final SeatService seatService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StandardResponseDto> createBooking(
            @RequestBody RequestBookingDto dto,
            HttpSession session
    ) {
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");

        if (loggedUserRole.equals("ADMIN") || loggedUserRole.equals("USER")) {

            ResponseBookingDto bookingDto = bookingService
                    .createBooking(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StandardResponseDto(201, "Booking created successfully", bookingDto));
        }

        return new ResponseEntity<>(
                new StandardResponseDto(401, "Unauthorized: No active session", null),
                HttpStatus.UNAUTHORIZED
        );

    }


    @PostMapping("/cancel/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StandardResponseDto> cancelBooking(
            @PathVariable Long bookingId,
            HttpSession session
    ) {
        if (session == null) {
            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(
                new StandardResponseDto(200, "Booking cancelled successfully", null)
        );
    }

    // ===== LIST BOOKINGS =====
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<StandardResponseDto> listBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserIdStr = (String) session.getAttribute("loggedUserId");

        if (loggedUserRole == null || loggedUserIdStr == null) {
            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        BookingPaginateResponseDto response;
        if ("ADMIN".equalsIgnoreCase(loggedUserRole)) {
            response = bookingService.listBookingsByUser(page, size, loggedUserIdStr);
        } else {

            response = bookingService.listBookingsByUser(page, size, loggedUserIdStr);
        }

        return ResponseEntity.ok(
                new StandardResponseDto(200, "Bookings fetched successfully", response)
        );
    }

    // ===== GET AVAILABLE SEATS =====
    @GetMapping("/available/{showtimeId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<StandardResponseDto> getAvailableSeats(
            @PathVariable Long showtimeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        AvailablePaginateResponseDto response = bookingService.getAvailableSeats(page, size, showtimeId);
        return new ResponseEntity<>(
                new StandardResponseDto(200, "Available seats fetched successfully", response)
                ,HttpStatus.OK
        );
    }

    @PutMapping("/update/{newShowtimeId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StandardResponseDto> updateBooking(
            @RequestBody RequestBookingDto dto,
            @PathVariable(required = false) Long newShowtimeId,

            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");

        if (loggedUserRole.equals("ADMIN") || loggedUserRole.equals("USER")) {
            ResponseBookingDto updatedBooking = bookingService
                    .updateBooking(dto, newShowtimeId);
            return ResponseEntity.ok(
                    new StandardResponseDto(200, "Booking updated successfully", updatedBooking)
            );
        }


        return new ResponseEntity<>(
                new StandardResponseDto(401, "Unauthorized: No active session", null),
                HttpStatus.UNAUTHORIZED
        );
    }

    @PostMapping("/confirm-seats")
    public ResponseEntity<?> confirmSeats(
            @RequestParam Long showtimeId,
            @RequestBody List<RequestSeatSelectionDto> seats) {

        if (seats.isEmpty()) {
            return ResponseEntity.badRequest().body("No seats selected");
        }

        try {
            BigDecimal totalPrice = seatService.bookSeats(showtimeId, seats);
            return ResponseEntity.ok("Seats successfully booked. Total Price: $" + totalPrice);
        } catch (DuplicateEntryException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
