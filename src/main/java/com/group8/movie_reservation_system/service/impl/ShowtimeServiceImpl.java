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
    private final HallRepository hallRepository;
    private final SystemLogService logService;

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
                .version(0)
                .build();

        Showtime saved = showtimeRepository.save(showtime);
        logService.logAction(adminId, "Created showtime ID: " + saved.getId());
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

        existing.setMovie(movie);
        existing.setHall(hall);
        existing.setDate(dto.getDate());
        existing.setTime(dto.getTime());

        Showtime saved = showtimeRepository.save(existing);
        logService.logAction(adminId, "Updated showtime ID: " + saved.getId());
        return mapToResponseDto(saved);
    }

    @Override
    public List<ResponseShowtimeDto> getAllShowTimes() {
        return showtimeRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteShowtime(Long id, String adminId) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + id));

        // Optional: check if bookings exist to prevent FK constraint
//        if (!showtime.getBookings().isEmpty()) {
//            throw new RuntimeException("Cannot delete showtime with existing bookings.");
//        }

        showtimeRepository.deleteById(id);
        logService.logAction(adminId, "Deleted showtime ID: " + id);
    }

    @Override
    public long getShowtimeCount() {
        return showtimeRepository.count();
    }

    @Override
    public ResponseShowtimeDto getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + id));
        return mapToResponseDto(showtime);
    }

    @Override
    public List<ResponseShowtimeDto> getShowTimesByMovieId(Long movieId) {
        return showtimeRepository.findByMovieId(movieId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ===== Helper mapping methods =====

    private ResponseShowtimeDto mapToResponseDto(Showtime showtime) {
        return ResponseShowtimeDto.builder()
                .showtimeId(showtime.getId())
                .movie(toResponseMovieDto(showtime.getMovie()))
                .title(showtime.getMovie().getTitle())
                .hall(toResponseHallDto(showtime.getHall()))
                .date(showtime.getDate())
                .time(showtime.getTime())
                .price(showtime.getPrice())
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

    // ===== DTO → Entity safe conversions (fetch existing DB entities) =====

    private Showtime toShowTime(ResponseShowtimeDto dto) {
        Movie movie = movieRepository.findById(dto.getMovie().getId())
                .orElseThrow(() -> new EntryNotFoundException("Movie not found with ID: " + dto.getMovie().getId()));

        Hall hall = hallRepository.findById(dto.getHall().getHallId())
                .orElseThrow(() -> new EntryNotFoundException("Hall not found with ID: " + dto.getHall().getHallId()));

        return Showtime.builder()
                .id(dto.getShowtimeId())
                .movie(movie)
                .hall(hall)
                .date(dto.getDate())
                .time(dto.getTime())
                .price(dto.getPrice())
                .build();
    }

    private Movie toMovie(ResponseMovieDto dto) {
        return movieRepository.findById(dto.getId())
                .orElseThrow(() -> new EntryNotFoundException("Movie not found with ID: " + dto.getId()));
    }

    private Hall toHall(ResponseHallDto dto) {
        return hallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new EntryNotFoundException("Hall not found with ID: " + dto.getHallId()));
    }

    private Seat toSeat(ResponseSeatDto dto) {
        return Seat.builder()
                .seatId(dto.getSeatId())
                .type(dto.getType())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .seatNumber(dto.getSeatNumber())
                .colNum(dto.getColNum())
                .rowNum(dto.getRowNum())
                .layout(dto.getLayout())
                .build();
    }

    private Booking toBooking(ResponseBookingDto dto) {
        Showtime showtime = toShowTime(dto.getShowtime());

        List<Seat> seats = dto.getSeats() != null
                ? dto.getSeats().stream().map(this::toSeat).collect(Collectors.toList())
                : Collections.emptyList();

        return Booking.builder()
                .id(dto.getBookingId())
                .showtime(showtime)
                .seats(seats)
                .timestamp(dto.getTimestamp())
                .status(dto.getStatus())
                .build();
    }
}
