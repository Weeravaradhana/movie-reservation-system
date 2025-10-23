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

    // ===== CREATE BOOKING =====
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StandardResponseDto> createBooking(
            @RequestBody RequestBookingDto dto,
            HttpSession session
    ) {
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");

        if (loggedUserRole == null || loggedUserId == null) {
            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: Please log in first", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {
            dto.setUserId(loggedUserId);
            ResponseBookingDto bookingDto = bookingService.createBooking(dto, loggedUserId);

            // === Payment flow trigger (frontend will redirect after receiving this response)
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StandardResponseDto(201,
                            "Booking created successfully. Proceed to payment.", bookingDto));
        }

        return new ResponseEntity<>(
                new StandardResponseDto(401, "Unauthorized: Invalid role", null),
                HttpStatus.UNAUTHORIZED
        );
    }

    // ===== CANCEL BOOKING =====
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
        String loggedUserId = (String) session.getAttribute("loggedUserId");

        if (loggedUserRole == null || loggedUserId == null) {
            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: Please log in", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        BookingPaginateResponseDto response =
                bookingService.listBookingsByUser(page, size, loggedUserId);

        return ResponseEntity.ok(
                new StandardResponseDto(200, "Bookings fetched successfully", response)
        );
    }

    // ===== AVAILABLE SEATS =====
    @GetMapping("/available/{showtimeId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<StandardResponseDto> getAvailableSeats(
            @PathVariable Long showtimeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        AvailablePaginateResponseDto response = bookingService.getAvailableSeats(page, size, showtimeId);
        return new ResponseEntity<>(
                new StandardResponseDto(200, "Available seats fetched successfully", response),
                HttpStatus.OK
        );
    }

    // ===== UPDATE BOOKING =====
    @PutMapping("/update/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StandardResponseDto> updateBooking(
            @RequestBody RequestBookingDto dto,
            @PathVariable Long bookingId,
            HttpSession session
    ) {
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");

        if (loggedUserRole == null) {
            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: Please log in", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        ResponseBookingDto updatedBooking = bookingService.updateBooking(dto, bookingId);
        return ResponseEntity.ok(
                new StandardResponseDto(200, "Booking updated successfully", updatedBooking)
        );
    }

    // ===== CONFIRM SEATS (Before Payment) =====
    @PostMapping("/confirm-seats")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StandardResponseDto> confirmSeats(
            @RequestParam Long showtimeId,
            @RequestBody List<RequestSeatSelectionDto> seats
    ) {
        if (seats.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new StandardResponseDto(400, "No seats selected", null));
        }

        try {
            BigDecimal totalPrice = seatService.bookSeats(showtimeId, seats);
            return ResponseEntity.ok(
                    new StandardResponseDto(200,
                            "Seats confirmed. Proceed to payment.",
                            totalPrice)
            );
        } catch (DuplicateEntryException e) {
            return ResponseEntity.status(409)
                    .body(new StandardResponseDto(409, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500)
                    .body(new StandardResponseDto(500, e.getMessage(), null));
        }
    }

}
