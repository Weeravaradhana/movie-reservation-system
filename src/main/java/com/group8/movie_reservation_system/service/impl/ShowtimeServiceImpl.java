package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestShowtimeDto;
import com.group8.movie_reservation_system.dto.response.*;
import com.group8.movie_reservation_system.entity.*;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.HallRepository;
import com.group8.movie_reservation_system.repo.MovieRepository;
import com.group8.movie_reservation_system.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final com.group8.movie_reservation_system.repository.ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final SystemLogService logService;
    private final HallRepository hallRepository;


    @Override
    public ResponseShowtimeDto createShowtime(RequestShowtimeDto dto, String adminId) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new EntryNotFoundException("Movie not found with ID: " + dto.getMovieId()));

        Hall hall = hallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new EntryNotFoundException("Hall not found with ID: " + dto.getHallId()));

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .hall(hall)
                .date(dto.getDate())
                .time(dto.getTime())
                .version(0) // 🔹 initial version assign
                .build();

        Showtime saved = showtimeRepository.save(showtime);
        logService.logAction(adminId, "Created showtime ID: " + saved.getId());
        System.out.println(saved.getId());
        return mapToResponseDto(saved);
    }

    @Override
    public ResponseShowtimeDto updateShowtime(Long id, RequestShowtimeDto dto, String adminId) {
        Showtime existing = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + id));

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + dto.getMovieId()));

        Hall hall = hallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new EntryNotFoundException("Hall not found with ID: " + dto.getHallId()));

        // existing entity update කරනවා
        existing.setMovie(movie);
        existing.setHall(hall);
        existing.setDate(dto.getDate());
        existing.setTime(dto.getTime());

        Showtime saved = showtimeRepository.save(existing); // version-safe save
        logService.logAction(adminId, "Updated showtime ID: " + saved.getId());

        return mapToResponseDto(saved);
    }

    public List<ResponseShowtimeDto> getAllShowTimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }


    public void deleteShowtime(Long id, String adminId) {
        showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + id));

        showtimeRepository.deleteById(id);
        logService.logAction(adminId, "Deleted showtime ID: " + id);
    }

    public long getShowtimeCount() {
        return showtimeRepository.count();
    }

    @Override
    public ResponseShowtimeDto getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + id));

        return ResponseShowtimeDto.builder()
                .showtimeId(showtime.getId())
                .date(showtime.getDate())
                .time(showtime.getTime())
                .price(showtime.getPrice())
                .movie(ResponseMovieDto.builder()
                        .id(showtime.getMovie().getId())
                        .title(showtime.getMovie().getTitle())
                        .build())
                .hall(ResponseHallDto.builder()
                        .hallId(showtime.getHall().getHallId())
                        .build())
                .build();
    }

    @Override
    public List<ResponseShowtimeDto> getShowTimesByMovieId(Long movieId) {
        return List.of();
    }

    private ResponseShowtimeDto mapToResponseDto(Showtime showtime) {
        return ResponseShowtimeDto.builder()
                .showtimeId(showtime.getId())
                .movie(toResponseMovieDto(showtime.getMovie()))
                .title(showtime.getMovie().getTitle())
                .hall(toResponseHallDto(showtime.getHall()))
                .date(showtime.getDate())
                .time(showtime.getTime())
                .build();
    }

    private Showtime toEntity(RequestShowtimeDto dto, Movie movie) {
        return Showtime.builder()
                .movie(movie)
                .id(dto.getHallId())
                .date(dto.getDate())
                .time(dto.getTime())
                .build();
    }

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
                .showtime(movie.getShowtimes() != null
                        ? movie.getShowtimes().stream()
                        .map(st -> ResponseShowtimeDto.builder()
                                .showtimeId(st.getId())
                                .date(st.getDate())
                                .time(st.getTime())
                                .hall(toResponseHallDto(st.getHall()))
                                .build())
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }


    private ResponseHallDto toResponseHallDto(Hall hall) {
        return ResponseHallDto.builder()
                .hallId(hall.getHallId())
                .type(hall.getType())
                .name(hall.getName())
                .capacity(hall.getCapacity())
                .build();
    }

    private Hall toHall(ResponseHallDto dto) {
        return Hall.builder()
                .hallId(dto.getHallId())
                .name(dto.getName())
                .type(dto.getType())
                .capacity(dto.getCapacity())
                .seats(dto.getSeats().stream().map(this::toSeat).collect(Collectors.toList()))
                .build();
    }


    private Seat toSeat(ResponseSeatDto dto) {
        return Seat.builder()
                .seatId(dto.getSeatId())
                .type(dto.getType())
                .price(dto.getPrice())
                .booking(toBooking(dto.getBooking()))
                .category(dto.getCategory())
                .seatNumber(dto.getSeatNumber())
                .colNum(dto.getColNum())
                .rowNum(dto.getRowNum())
                .layout(dto.getLayout())
                .build();
    }

    private Booking toBooking(ResponseBookingDto dto) {
        return Booking.builder()
                .id(dto.getBookingId())
                .showtime(toShowTime(dto.getShowtime()))
                .seats(dto.getSeats().stream().map(this::toSeat).collect(Collectors.toList()))
                .timestamp(dto.getTimestamp())
                .status(dto.getStatus())
                .build();
    }

    private Showtime toShowTime(ResponseShowtimeDto dto){
        return  Showtime.builder()
                .id(dto.getShowtimeId())
                .movie(toMovie(dto.getMovie()))
                .price(dto.getPrice())
                .date(dto.getDate())
                .price(dto.getPrice())
                .time(dto.getTime())
                .hall(toHall(dto.getHall()))
                .build();
    }

    private Movie toMovie(ResponseMovieDto dto) {
        return Movie.builder()
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .genre(dto.getGenre())
                .rating(dto.getRating())
                .trailer_url(dto.getPosterUrl())
                .status(dto.getStatus())
                .showtimes(dto.getShowtime().stream().map(this::toShowTime).collect(Collectors.toList()))
                .build();
    }

}
