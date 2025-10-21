package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseReviewDto;
import com.group8.movie_reservation_system.dto.response.ResponseShowtimeDto;
import com.group8.movie_reservation_system.dto.response.paginate.MoviePaginateResponseDto;
import com.group8.movie_reservation_system.entity.Movie;
import com.group8.movie_reservation_system.entity.Showtime;
import com.group8.movie_reservation_system.entity.Review;
import com.group8.movie_reservation_system.repo.MovieRepository;
import com.group8.movie_reservation_system.repo.ReviewRepository;
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
    private final com.group8.movie_reservation_system.repository.ShowtimeRepository showtimeRepository;
    private final ReviewRepository reviewRepository;
    private final SystemLogService logService;

    // ================== CREATE MOVIE ==================
    public ResponseMovieDto createMovie(RequestMovieDto dto, String adminId) {
        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .genre(dto.getGenre())
                .rating(dto.getRating())
                .trailer_url(dto.getPosterUrl())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Movie saved = movieRepository.save(movie);
        logService.logAction(adminId, "Created movie: " + saved.getTitle());

        return toResponseMovieDto(saved);
    }

    // ================== GET ALL MOVIES (PAGINATED) ==================
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

    // ================== GET MOVIE BY ID ==================
    public ResponseMovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
        return toResponseMovieDto(movie);
    }

    // ================== UPDATE MOVIE ==================
    public ResponseMovieDto updateMovie(Long id, RequestMovieDto dto, String adminId) {
        Movie updatedMovie = movieRepository.findById(id).map(movie -> {
            movie.setTitle(dto.getTitle());
            movie.setDescription(dto.getDescription());
            movie.setDuration(dto.getDuration());
            movie.setGenre(dto.getGenre());
            movie.setRating(dto.getRating());
            movie.setTrailer_url(dto.getPosterUrl());
            movie.setStatus(dto.getStatus() != null ? dto.getStatus() : movie.getStatus());

            Movie saved = movieRepository.save(movie);
            logService.logAction(adminId, "Updated movie: " + saved.getTitle());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));

        return toResponseMovieDto(updatedMovie);
    }

    // ================== DELETE MOVIE ==================
    public void deleteMovie(Long id, String adminId) {
        movieRepository.findById(id).ifPresentOrElse(movie -> {
            movieRepository.deleteById(id);
            logService.logAction(adminId, "Deleted movie: " + movie.getTitle());
        }, () -> {
            throw new RuntimeException("Movie not found with ID: " + id);
        });
    }

    // ================== GET TOTAL COUNT ==================
    public long getMovieCount() {
        return movieRepository.count();
    }

    @Override
    public MoviePaginateResponseDto findAllMovies(Pageable pageable) {
        return null;
    }

    @Override
    public MoviePaginateResponseDto searchMovies(String searchTerm, Pageable pageable) {
        return null;
    }

    @Override
    public List<ResponseShowtimeDto> getShowtimesByMovie(Long movieId) {
        List<Showtime> showtimes = showtimeRepository.findByMovieId(movieId);

        return showtimes.stream()
                .map(showtime -> ResponseShowtimeDto.builder()
                        .showtimeId(showtime.getId())
                        .date(showtime.getDate())
                        .time(showtime.getTime())
                        .price(showtime.getPrice())
                        .movie(ResponseMovieDto.builder()
                                .id(showtime.getMovie().getId())
                                .title(showtime.getMovie().getTitle())
                                .description(showtime.getMovie().getDescription())
                                .duration(showtime.getMovie().getDuration())
                                .genre(showtime.getMovie().getGenre())
                                .rating(showtime.getMovie().getRating())
                                .posterUrl(showtime.getMovie().getTrailer_url())
                                .status(showtime.getMovie().getStatus())
                                .build())
                        .hall(ResponseHallDto.builder()
                                .hallId(showtime.getHall().getHallId())
                                .name(showtime.getHall().getName())
                                .capacity(showtime.getHall().getCapacity())
                                .build())
                        .build()
                ).collect(Collectors.toList());
    }


    @Override
    public List<ResponseReviewDto> getReviewsByMovie(Long movieId) {
        // 🔹 Movie එකට අදාළ reviews DB එකෙන් ගන්නවා
        List<Review> reviews = reviewRepository.findByMovieId(movieId);

        // 🔹 Review list එක DTO list එකකට map කරනවා
        return reviews.stream().map(review -> ResponseReviewDto.builder()
                .id(review.getId())
                .content(review.getContent())                  // review content
                .rating(review.getRating())                    // review rating
                .status(review.getStatus().name())            // review status (PENDING, APPROVED, REJECTED)
                .userId(review.getUser().getId().toString())  // user ID string එකට convert කරන්න
                .userName(review.getUser().getFullName())     // user name
                .adminResponse(review.getAdminResponse())     // admin response
                .adminResponseAt(review.getAdminResponseAt()) // admin response time
                .createdAt(review.getCreatedAt())             // created at
                .updatedAt(review.getUpdatedAt())             // updated at
                .build()
        ).collect(Collectors.toList());
    }


    // ================== Helper DTO Mapper ==================
    private ResponseMovieDto toResponseMovieDto(Movie movie) {
        return ResponseMovieDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .genre(movie.getGenre())
                .rating(movie.getRating())
                .posterUrl(movie.getTrailer_url())
                .status(movie.getStatus())
                .build();
    }
}
