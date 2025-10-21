package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestSeatSelectionDto;
import com.group8.movie_reservation_system.dto.response.ResponseHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseMovieDto;
import com.group8.movie_reservation_system.dto.response.ResponseSeatDto;
import com.group8.movie_reservation_system.entity.*;
import com.group8.movie_reservation_system.exception.DuplicateEntryException;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.*;
import com.group8.movie_reservation_system.service.SeatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final DealRepo dealRepo;
    private final MovieRepository movieRepository;
    private final com.group8.movie_reservation_system.repository.ShowtimeRepository showtimeRepository;

    @PostConstruct
    public void initializeHalls() {
        try {
            if (hallRepository.count() == 0) {
                System.out.println("[INIT] Creating default hall...");
                hallRepository.save(new Hall("Main Hall", 200, "balcony"));
            }

            Hall hall = hallRepository.findAll().get(0);
            fixHallDefaults(hall);

            long existingSeats = seatRepository.countByHall_HallId(hall.getHallId());
            if (existingSeats == 0 || existingSeats != hall.getCapacity()) {
                System.out.println("[INIT] Generating seats for hall: " + hall.getName());
                seatAllocationRepository.deleteByHallId(hall.getHallId());
                seatRepository.deleteByHallId(hall.getHallId());
                initializeSeats(hall);
            }

            System.out.println("[INIT] Hall initialization completed successfully!");
        } catch (Exception e) {
            System.err.println("[INIT ERROR] " + e.getMessage());
        }
    }

    private void fixHallDefaults(Hall hall) {
        boolean updated = false;

        if (!"Main Hall".equals(hall.getName())) { hall.setName("Main Hall"); updated = true; }
        if (hall.getCapacity() != 200) { hall.setCapacity(200); updated = true; }
        if (!"balcony".equalsIgnoreCase(hall.getType())) { hall.setType("balcony"); updated = true; }

        if (updated) hallRepository.save(hall);
    }

    @PostConstruct
    public void initHallsAndSeats() {
        try {
            if (hallRepository.count() == 0) {
                System.out.println("[INIT] Creating default hall...");
                Hall hall = new Hall("Main Hall", 200, "balcony");
                hallRepository.save(hall);
                initializeSeats(hall);
            } else {
                Hall hall = hallRepository.findAll().get(0);
                long seatCount = seatRepository.countByHall_HallId(hall.getHallId());
                if (seatCount != hall.getCapacity()) {
                    System.out.println("[INIT] Re-initializing seats for hall: " + hall.getName());
                    seatRepository.deleteByHallId(hall.getHallId());
                    initializeSeats(hall);
                }
            }
            System.out.println("[INIT] Halls and seats initialization complete.");
        } catch (Exception e) {
            System.err.println("[INIT ERROR] " + e.getMessage());
        }
    }

    public List<Seat> getSeatsByHallId(Long hallId) {
        return seatRepository.findByHallId(hallId);
    }

    @Transactional
    public void bookSeats(List<Long> seatIds) {
        for(Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));
            if(!seat.isBooked()) {
                seat.setBooked(true); // booked=false → true
                seat.setStatus("Booked"); // optional
                seatRepository.save(seat);
            } else {
                throw new RuntimeException("Seat already booked: " + seat.getSeatNumber());
            }
        }
    }

    @Override
    public ResponseMovieDto getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntryNotFoundException("Movie not found with ID: " + movieId));

        return ResponseMovieDto.builder()
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .genre(movie.getGenre())
                .rating(movie.getRating())
                .posterUrl(movie.getTrailer_url())
                .build();
    }



    @Override
    public void initializeSeats(Hall hall) {
        List<Seat> seats = new ArrayList<>();
        int total = hall.getCapacity();
        int boxSeats = 40;
        int standardSeats = total - boxSeats;

        // Box seats
        for (int i = 1; i <= boxSeats; i++) {
            seats.add(new Seat(hall, "BX" + i, (i % 2 == 0) ? "Couple" : "Normal",
                    "box", "box", false, 0, i, BigDecimal.valueOf(25.0)));
        }

        // Standard seats
        String[] layouts = ("balcony".equalsIgnoreCase(hall.getType()))
                ? new String[]{"front", "middle", "back", "balcony"}
                : new String[]{"front", "middle", "back"};
        int seatsPerLayout = standardSeats / layouts.length;
        int seatCounter = 1;

        for (String layout : layouts) {
            BigDecimal price = calculatePrice(layout, hall.getType());
            for (int i = 1; i <= seatsPerLayout; i++) {
                String seatNumber = layout.substring(0, 1).toUpperCase() + seatCounter;
                String type = (i % 10 == 0) ? "Couple" : "Normal";
                seats.add(new Seat(hall, seatNumber, type, "standard", layout,
                        false, i / 10, i, price));
                seatCounter++;
            }
        }

        seatRepository.saveAll(seats);
    }

    private BigDecimal calculatePrice(String layout, String hallType) {
        return switch (layout) {
            case "front" -> BigDecimal.valueOf(hallType.equalsIgnoreCase("balcony") ? 18.0 : 15.0);
            case "middle" -> BigDecimal.valueOf(hallType.equalsIgnoreCase("balcony") ? 15.0 : 12.0);
            case "back" -> BigDecimal.valueOf(hallType.equalsIgnoreCase("balcony") ? 12.0 : 10.0);
            case "balcony" -> BigDecimal.valueOf(22.0);
            case "box" -> BigDecimal.valueOf(hallType.equalsIgnoreCase("balcony") ? 25.0 : 20.0);
            default -> BigDecimal.valueOf(12.0);
        };
    }

    @Override
    public void allocateSeatForShowtime(Long seatId, Long showtimeId, String status) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat ID not found " + seatId));
        List<SeatAllocation> existing = seatAllocationRepository.lockBySeatAndShowtime(seatId, showtimeId);

        SeatAllocation allocation = existing.isEmpty()
                ? new SeatAllocation(seat, showtimeId, status)
                : existing.get(0);

        allocation.setStatus(status);
        seatAllocationRepository.saveAndFlush(allocation);
        updateSeatStatus(seatId, status);
    }

    @Override
    public boolean isSeatBookedForShowtime(Long seatId, Long showtimeId) {
        return seatAllocationRepository.findBySeat_SeatIdAndShowtimeId(seatId, showtimeId)
                .stream()
                .anyMatch(a -> "booked".equalsIgnoreCase(a.getStatus()));
    }


    @Override
    public ResponseSeatDto updateSeatStatus(Long seatId, String status) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(()-> new EntryNotFoundException("Seat id not found: " + seatId));
        seat.setStatus(status);
        return toResponseSeatDto(seatRepository.save(seat));
    }

    @Override
    public void deleteSeat(Long seatId) {
        seatAllocationRepository.deleteAll(seatAllocationRepository.findById(seatId).stream().toList());
        seatRepository.deleteById(seatId);
    }

    @Override
    public SeatAllocation bookSeatWithDeal(Long seatId, Long showtimeId,int promoCode) {
        if (isSeatBookedForShowtime(seatId, showtimeId)) {
            throw new DuplicateEntryException("Seat is already booked!");
        }
        allocateSeatForShowtime(seatId, showtimeId, "booked");
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new EntryNotFoundException("Seat ID not found " + seatId));

        Deal dealsByCode = dealRepo.findDealsByCode(promoCode)
                .orElseThrow(()-> new EntryNotFoundException("Promo code not found " + promoCode));
        double finalPrice = seat.getPrice().doubleValue();

        if (dealsByCode != null) {
            double discountAmount = finalPrice * (dealsByCode.getDiscount() / 100);
            finalPrice = finalPrice - discountAmount;
        }

        SeatAllocation allocation = seatAllocationRepository.findBySeat_SeatIdAndShowtimeId(seatId, showtimeId)
                .stream().findFirst()
                .orElseThrow(() -> new EntryNotFoundException("SeatAllocation not found after allocation!"));

        allocation.setStatus("booked");
        allocation.setPrice(BigDecimal.valueOf(finalPrice));
        allocation.setDeal(dealsByCode);
        return seatAllocationRepository.save(allocation);
    }

    @Override
    public ResponseSeatDto getSeatById(Long seatId) {
        return seatRepository.findById(seatId).map(this::toResponseSeatDto)
                .orElseThrow(() -> new EntryNotFoundException("Seat not found with ID: " + seatId));
    }

    @Override
    public ResponseHallDto getHallById(Long hallId) {
        return hallRepository.findById(hallId).map(this::toResponseHallDto)
                .orElseThrow(() -> new EntryNotFoundException("Hall not found with ID: " + hallId));
    }

    @Override
    public ResponseHallDto getDefaultHall(Long showtimeId) {
        return null;
    }

    @Override
    public List<ResponseSeatDto> getAllSeats() {

        return seatRepository.findAll().stream().map(this::toResponseSeatDto).toList();
    }


    @Override
    public ResponseHallDto getDefaultHallForShowtime(Long showtimeId) {
        // 1️⃣ Find showtime
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        Hall hall = showtime.getHall(); // Get the hall associated with the showtime

        // 2️⃣ Map seats to ResponseSeatDto
        List<ResponseSeatDto> seatDtos = hall.getSeats().stream()
                .map(seat -> ResponseSeatDto.builder()
                        .seatId(seat.getSeatId())
                        .seatNumber(seat.getSeatNumber())
                        .status(isSeatBookedForShowtime(seat.getSeatId(), showtimeId) ? "occupied" : "available")
                        .price(seat.getPrice())
                        .build())
                .toList();

        // 3️⃣ Build ResponseHallDto
        return ResponseHallDto.builder()
                .hallId(hall.getHallId())
                .name(hall.getName())
                .capacity(hall.getCapacity())
                .type(hall.getType())
                .seats(seatDtos)
                .build();
    }

    private ResponseSeatDto toResponseSeatDto(Seat seat) {
        return ResponseSeatDto.builder()
                .seatNumber(seat.getSeatNumber())
                .type(seat.getType())
                .category(seat.getCategory())
                .layout(seat.getLayout())
                .status(seat.getStatus())
                .rowNum(seat.getRowNum())
                .colNum(seat.getColNum())
                .price(seat.getPrice())
                .build();
    }

    private ResponseHallDto toResponseHallDto(Hall hall) {
        return ResponseHallDto.builder()
                .name(hall.getName())
                .capacity(hall.getCapacity())
                .type(hall.getType())
                .build();
    }

    private Deal getApplicableDeal(Integer promoCode) {
        Date now = new Date();
        return dealRepo.findAll().stream()
                .filter(e-> e.getCode()==promoCode)
                .filter(Deal::isStatus)
                .filter(deal -> !now.before(deal.getStartDate()) && !now.after(deal.getEndDate()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public BigDecimal bookSeats(Long showtimeId, List<RequestSeatSelectionDto> selectedSeats) {

        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + showtimeId));

        // Create a new Booking for this seat selection
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        // You can set other booking details like user, timestamp etc. if needed

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (RequestSeatSelectionDto seatDto : selectedSeats) {
            Seat seat = seatRepository.findById(seatDto.getSeatId())
                    .orElseThrow(() -> new RuntimeException("Seat not found with id: " + seatDto.getSeatId()));

            if (seat.isBooked()) {
                throw new DuplicateEntryException("Seat " + seat.getSeatNumber() + " is already booked.");
            }

            seat.setBooked(true);
            seat.setBooking(booking);
            seatRepository.save(seat);

            totalPrice = totalPrice.add(seat.getPrice());
        }

        // Optional service fee
        BigDecimal serviceFee = BigDecimal.valueOf(2.00);
        totalPrice = totalPrice.add(serviceFee);

        return totalPrice;
    }

    @Override
    public List<Seat> getSeatsByShowtime(Long showtimeId) {
        Optional<Showtime> optionalShowtime = showtimeRepository.findById(showtimeId);
        if (optionalShowtime.isEmpty()) {
            throw new RuntimeException("Showtime not found with id: " + showtimeId);
        }
        return seatRepository.findByHallId(optionalShowtime.get().getHall().getHallId());
    }

}
