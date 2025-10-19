package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.paginate.MoviePaginateResponseDto;
import com.group8.movie_reservation_system.entity.Movie;
import com.group8.movie_reservation_system.repo.MovieRepository;
import com.group8.movie_reservation_system.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final SystemLogService logService;


    public ResponseMovieDto createMovie(RequestMovieDto dto, String adminId) {
        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .genre(dto.getGenre())
                .rating(dto.getRating())
                .trailer_url(dto.getTrailerUrl())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Movie saved = movieRepository.save(movie);

        logService.logAction(adminId, "Created movie: " + saved.getTitle());

        return toResponseMovieDto(saved);
    }

    public MoviePaginateResponseDto getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<ResponseMovieDto> dtoList = moviePage.getContent().stream()
                .map(this::toResponseMovieDto)
                .collect(Collectors.toList());

        return MoviePaginateResponseDto.builder()
                .dataList(dtoList)
                .dataCount(moviePage.getTotalElements())
                .build();
    }

    public ResponseMovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
        return toResponseMovieDto(movie);
    }

    public ResponseMovieDto updateMovie(Long id, RequestMovieDto dto, String adminId) {
        Movie updatedMovie = movieRepository.findById(id).map(movie -> {
            movie.setTitle(dto.getTitle());
            movie.setDescription(dto.getDescription());
            movie.setDuration(dto.getDuration());
            movie.setGenre(dto.getGenre());
            movie.setRating(dto.getRating());
            movie.setTrailer_url(dto.getTrailerUrl());
            movie.setStatus(dto.getStatus() != null ? dto.getStatus() : movie.getStatus());

            Movie saved = movieRepository.save(movie);

            logService.logAction(adminId, "Updated movie: " + saved.getTitle());

            return saved;
        }).orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));

        return toResponseMovieDto(updatedMovie);
    }

    public void deleteMovie(Long id, String adminId) {
        movieRepository.findById(id).ifPresentOrElse(movie -> {
            movieRepository.deleteById(id);
            logService.logAction(adminId, "Deleted movie: " + movie.getTitle());
        }, () -> {
            throw new RuntimeException("Movie not found with ID: " + id);
        });
    }

    // ================== GET TOTAL MOVIE COUNT ==================
    public long getMovieCount() {
        return movieRepository.count();
    }

    // ================== HELPER METHOD ==================
    private ResponseMovieDto toResponseMovieDto(Movie movie) {
        return ResponseMovieDto.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .genre(movie.getGenre())
                .rating(movie.getRating())
                .trailerUrl(movie.getTrailer_url())
                .status(movie.getStatus())
                .build();
    }
}
