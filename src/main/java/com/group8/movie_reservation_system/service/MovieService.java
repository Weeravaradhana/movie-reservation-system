package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.paginate.MoviePaginateResponseDto;

public interface MovieService {

    ResponseMovieDto createMovie(RequestMovieDto dto, String adminId);
    MoviePaginateResponseDto getAllMovies(int page, int size);
    ResponseMovieDto getMovieById(Long id);
    ResponseMovieDto updateMovie(Long id, RequestMovieDto dto, String adminId);
    void deleteMovie(Long id, String adminId);
    long getMovieCount();
}
