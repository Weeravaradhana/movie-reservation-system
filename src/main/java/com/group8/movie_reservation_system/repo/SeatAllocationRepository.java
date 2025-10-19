package com.group8.movie_reservation_system.repo;


import com.group8.movie_reservation_system.entity.Seat;
import com.group8.movie_reservation_system.entity.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {
    List<SeatAllocation> findByShowtimeId(Integer showtimeId);

    @Modifying
    @Transactional
    void deleteByShowtimeId(Long showtimeId);

    @Modifying
    @Transactional
    void deleteByShowtimeIdAndSeat_SeatId(Long showtimeId, Long seatId);


    @Modifying
    @Transactional
    @Query("DELETE FROM SeatAllocation sa WHERE sa.seat.hall.hallId = :hallId")
    void deleteByHallId(@Param("hallId") Long hallId);

    List<SeatAllocation> findBySeat_Hall_HallId(Long hallId);

    // FIXED: Added missing method
    List<SeatAllocation> findBySeat_SeatIdAndShowtimeId(Long seatId, Long showtimeId);

    // Lock a seat allocation row for a given seat and showtime to prevent races
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sa FROM SeatAllocation sa WHERE sa.seat.seatId = :seatId AND sa.showtimeId = :showtimeId")
    List<SeatAllocation> lockBySeatAndShowtime(@Param("seatId") Long seatId, @Param("showtimeId") Long showtimeId);

    @Query("SELECT sa FROM SeatAllocation sa WHERE sa.seat.hall.hallId = :hallId AND sa.showtimeId = :showtimeId")
    List<SeatAllocation> findByHallAndShowtime(@Param("hallId") Long hallId, @Param("showtimeId") Long showtimeId);


}