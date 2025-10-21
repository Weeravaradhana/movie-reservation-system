package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseReviewDto;
import com.group8.movie_reservation_system.dto.response.ResponseShowtimeDto;
import com.group8.movie_reservation_system.dto.response.paginate.MoviePaginateResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {

    ResponseMovieDto createMovie(RequestMovieDto dto, String adminId);
    MoviePaginateResponseDto getAllMovies(int page, int size);
    ResponseMovieDto getMovieById(Long id);
    ResponseMovieDto updateMovie(Long id, RequestMovieDto dto, String adminId);
    void deleteMovie(Long id, String adminId);
    long getMovieCount();
    List<ResponseShowtimeDto> getShowtimesByMovie(Long movieId);
    List<ResponseReviewDto> getReviewsByMovie(Long movieId);
    // Additional methods for Thymeleaf integration
    MoviePaginateResponseDto findAllMovies(Pageable pageable);
    MoviePaginateResponseDto searchMovies(String searchTerm, Pageable pageable);
}
