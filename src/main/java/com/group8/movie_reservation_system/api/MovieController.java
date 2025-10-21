package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.paginate.MoviePaginateResponseDto;
import com.group8.movie_reservation_system.service.MovieService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movie-management-service/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/admin/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> createMovie(
            @RequestBody RequestMovieDto dto,
            HttpSession session
    ) {
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }

        ResponseMovieDto created = movieService.createMovie(dto,loggedUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponseDto(201, "Movie created successfully", created));
    }

    @GetMapping("/visitors/find-all")
    public ResponseEntity<StandardResponseDto> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MoviePaginateResponseDto movies = movieService.getAllMovies(page, size);
        return ResponseEntity.ok(
                new StandardResponseDto(200, "Movies retrieved successfully", movies)
        );
    }

    @GetMapping("/visitor/{id}")
    public ResponseEntity<StandardResponseDto> getMovieById(@PathVariable Long id) {

        ResponseMovieDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(
                new StandardResponseDto(200, "Movie retrieved successfully", movie)
        );
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> updateMovie(
            @PathVariable Long id,
            @RequestBody RequestMovieDto dto,
            HttpSession session
    ) {


        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }

        ResponseMovieDto updated = movieService.updateMovie(id, dto, loggedUserId);
        return ResponseEntity.ok(
                new StandardResponseDto(200, "Movie updated successfully", updated)
        );
    }


    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteMovie(
            @PathVariable Long id,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }

        movieService.deleteMovie(id, loggedUserId);
        return ResponseEntity.ok(
                new StandardResponseDto(200, "Movie deleted successfully", null)
        );
    }


    @GetMapping("/visitor/count")
    public ResponseEntity<StandardResponseDto> getMovieCount() {
        long count = movieService.getMovieCount();
        return ResponseEntity.ok(
                new StandardResponseDto(200, "Total movies count retrieved", count)
        );
    }
}
