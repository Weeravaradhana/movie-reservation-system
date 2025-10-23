package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseShowtimeDto;
import com.group8.movie_reservation_system.dto.response.ResponseReviewDto;
import com.group8.movie_reservation_system.dto.response.paginate.MoviePaginateResponseDto;
import com.group8.movie_reservation_system.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie-details-management-service/api/v1/movies")
@RequiredArgsConstructor
public class MovieDetailsController {

    private final MovieService movieService;


    @GetMapping
    public ResponseEntity<MoviePaginateResponseDto> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        MoviePaginateResponseDto response = movieService.getAllMovies(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ResponseMovieDto> getMovieById(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getMovieById(movieId));
    }

    @GetMapping("/{movieId}/showtimes")
    public ResponseEntity<List<ResponseShowtimeDto>> getShowtimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getShowtimesByMovie(movieId));
    }

    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<List<ResponseReviewDto>> getReviewsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getReviewsByMovie(movieId));
    }
}
