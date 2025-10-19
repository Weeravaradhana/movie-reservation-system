package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestShowtimeDto;
import com.group8.movie_reservation_system.dto.response.ResponseShowtimeDto;
import com.group8.movie_reservation_system.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ResponseShowtimeDto> createShowtime(
            @RequestBody RequestShowtimeDto dto,
            @RequestHeader("Admin-Id") String adminId) {
        ResponseShowtimeDto created = showtimeService.createShowtime(dto, adminId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ResponseShowtimeDto>> getAllShowtimes() {
        List<ResponseShowtimeDto> showtimes = showtimeService.getAllShowTimes();
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseShowtimeDto> getShowtimeById(@PathVariable Long id) {
        ResponseShowtimeDto showtime = showtimeService.getAllShowTimes()
                .stream()
                .filter(s -> s.getShowtimeId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + id));
        return ResponseEntity.ok(showtime);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseShowtimeDto> updateShowtime(
            @PathVariable Long id,
            @RequestBody RequestShowtimeDto dto,
            @RequestHeader("Admin-Id") String adminId) {
        ResponseShowtimeDto updated = showtimeService.updateShowtime(id, dto, adminId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(
            @PathVariable Long id,
            @RequestHeader("Admin-Id") String adminId) {
        showtimeService.deleteShowtime(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getShowtimeCount() {
        long count = showtimeService.getShowtimeCount();
        return ResponseEntity.ok(count);
    }
}
