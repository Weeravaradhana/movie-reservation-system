package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseHallDto;
import com.group8.movie_reservation_system.dto.response.ResponseSeatDto;
import com.group8.movie_reservation_system.entity.Deal;
import com.group8.movie_reservation_system.entity.Hall;
import com.group8.movie_reservation_system.entity.Seat;
import com.group8.movie_reservation_system.entity.SeatAllocation;
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



@Service
@Transactional
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final DealRepo dealRepo;

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

    @Override
    public void initializeSeats(Hall hall) {
        List<Seat> seats = new ArrayList<>();
        int total = hall.getCapacity();
        int boxSeats = 40;
        int standardSeats = total - boxSeats;

        // Box seats
        for (int i = 1; i <= boxSeats; i++) {
            seats.add(new Seat(hall, "BX" + i, (i % 2 == 0) ? "Couple" : "Normal",
                    "box", "box", "available", 0, i, BigDecimal.valueOf(25.0)));
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
                        "available", i / 10, i, price));
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
    public ResponseHallDto getDefaultHallForShowtime(Long showtimeId) {
        return hallRepository.findAll().stream().map(this::toResponseHallDto)
                .findFirst()
                .orElseThrow(()-> new EntryNotFoundException("hall not found"));
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



}
