package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestBookingDto;
import com.group8.movie_reservation_system.dto.request.RequestUserDto;
import com.group8.movie_reservation_system.dto.response.*;
import com.group8.movie_reservation_system.dto.response.paginate.AvailablePaginateResponseDto;
import com.group8.movie_reservation_system.dto.response.paginate.BookingPaginateResponseDto;
import com.group8.movie_reservation_system.entity.*;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.BookingRepository;
import com.group8.movie_reservation_system.repo.SeatRepository;
import com.group8.movie_reservation_system.repo.ShowtimeRepository;
import com.group8.movie_reservation_system.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final SeatRepository seatRepo;
    private final BookingRepository bookingRepo;
    private final ShowtimeRepository showtimeRepo;

    // ===== CREATE BOOKING =====
    @Override
    public ResponseBookingDto createBooking(RequestBookingDto dto) {

        Showtime showtime = showtimeRepo.findById(dto.getShowtimeId())
                .orElseThrow(()-> new EntryNotFoundException("Show time not found"));

        List<Seat> seats = seatRepo
                .lockSeatsByHallAndSeatNumbers(showtime.getHall().getHallId().longValue(), dto.getSeatNumbers());

        Set<String> foundSeatNumbers = seats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toSet());

        for (Integer sNum : dto.getSeatNumbers()) {
            if (!foundSeatNumbers.contains(sNum)) {
                throw new EntryNotFoundException("Seat not found: " + sNum);
            }
        }

        for (Seat s : seats) {
            if ("BOOKED".equalsIgnoreCase(s.getStatus())) {
                throw new EntryNotFoundException("Seat already booked: " + s.getSeatNumber());
            }
        }

        Booking booking = new Booking();
        booking.setUser(toUserEntity(dto.getUser()));
        booking.setShowtime(showtime);
        booking.setTimestamp(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        for (Seat s : seats) {
            s.setStatus("BOOKED");
            booking.getSeats().add(s);
        }

        bookingRepo.save(booking);
        seatRepo.saveAll(seats);

        return mapToResponseBookingDto(booking);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntryNotFoundException("Booking not found"));

        for (Seat seat : booking.getSeats()) {
            seat.setStatus("AVAILABLE");
            seatRepo.save(seat);
        }

        booking.setStatus("CANCELLED");
        bookingRepo.save(booking);
    }

    @Override
    public BookingPaginateResponseDto listBookingsByUser(int page, int size, String userId) {
        List<Booking> bookings = bookingRepo.findByUserId(userId).stream()
                .filter(b -> !"CANCELLED".equalsIgnoreCase(b.getStatus()))
                .toList();

        List<ResponseBookingDto> dtoList = bookings.stream()
                .map(this::mapToResponseBookingDto)
                .toList();

        return new BookingPaginateResponseDto(dtoList, dtoList.size());
    }

    @Override
    public AvailablePaginateResponseDto getAvailableSeats(int page, int size, Long showtimeId) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new EntryNotFoundException("Showtime not found"));

        List<Seat> seats = seatRepo.findByHallId(showtime.getHall().getHallId().longValue());

        List<ResponseAvailableSeatsDto> dtoList = seats.stream()
                .map(s -> new ResponseAvailableSeatsDto(s.getSeatNumber(), s.getStatus()))
                .toList();

        return new AvailablePaginateResponseDto(dtoList, dtoList.size());
    }

    @Override
    public ResponseBookingDto updateBooking(RequestBookingDto dto,Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntryNotFoundException("Booking not found"));

        Showtime showtime = (dto.getShowtimeId() != null)
                ? showtimeRepo.findById(dto.getShowtimeId())
                .orElseThrow(() -> new EntryNotFoundException("Showtime not found"))
                : booking.getShowtime();

        for (Seat s : booking.getSeats()) {
            s.setStatus("AVAILABLE");
            seatRepo.save(s);
        }

        booking.getSeats().clear();

        List<Seat> newSeats = seatRepo
                .lockSeatsByHallAndSeatNumbers(showtime.getHall().getHallId().longValue(), dto.getSeatNumbers());

        for (Seat s : newSeats) {
            if ("BOOKED".equalsIgnoreCase(s.getStatus())) {
                throw new RuntimeException("Seat already booked: " + s.getSeatNumber());
            }
            s.setStatus("BOOKED");
        }

        booking.setShowtime(showtime);
        booking.getSeats().addAll(newSeats);

        bookingRepo.save(booking);
        seatRepo.saveAll(newSeats);

        return mapToResponseBookingDto(booking);
    }

    private ResponseBookingDto mapToResponseBookingDto(Booking booking) {

        ResponseShowtimeDto showtimeDto = ResponseShowtimeDto.builder()
                .showtimeId(booking.getShowtime().getId())
                .movie(toResponseMovieDto(booking.getShowtime().getMovie()))
                .hall(toResponseHallDto(booking.getShowtime().getHall()))
                .date(booking.getShowtime().getDate())
                .time(booking.getShowtime().getTime())
                .price(booking.getShowtime().getPrice())
                .build();


        List<ResponseSeatDto> seatDtos = booking.getSeats().stream()
                .map(seat -> ResponseSeatDto.builder()
                        .seatId(seat.getSeatId())
                        .seatNumber(seat.getSeatNumber())
                        .status(seat.getStatus())
                        .build())
                .toList();


        ResponseUserDto userDto = ResponseUserDto.builder()
                .id(booking.getUser().getId())
                .fullName(booking.getUser().getFullName())
                .username(booking.getUser().getUsername())
                .build();


        return ResponseBookingDto.builder()
                .bookingId(booking.getId())
                .status(booking.getStatus())
                .timestamp(booking.getTimestamp())
                .showtime(showtimeDto)
                .seats(seatDtos)
                .user(userDto)
                .build();
    }

    private ResponseMovieDto toResponseMovieDto(Movie movie) {
        if (movie == null) return null;

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

    private ResponseHallDto toResponseHallDto(Hall hall) {
        if (hall == null) return null;

        return ResponseHallDto.builder()
                .hallId(hall.getHallId())
                .name(hall.getName())
                .capacity(hall.getCapacity())
                .type(hall.getType())
                .build();
    }

    private RequestUserDto toRequestUserDto(User user) {
        if (user == null) return null;

        return RequestUserDto.builder()
                .fullName(user.getFullName())
                .username(user.getUsername())
                .build();
    }

    private User toUserEntity(RequestUserDto requestUserDto) {
        if (requestUserDto == null) return null;

        return User.builder()
                .id(java.util.UUID.randomUUID().toString()) // Unique ID generate
                .fullName(requestUserDto.getFullName())
                .username(requestUserDto.getUsername())
                .passwordHash(hashPassword(requestUserDto.getPassword())) // Password hash කරන්න
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    private String hashPassword(String password) {
        if (password == null) return null;
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password);
    }



}
