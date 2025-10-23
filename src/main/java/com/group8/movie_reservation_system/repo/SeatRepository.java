package com.group8.movie_reservation_system.repo;


import com.group8.movie_reservation_system.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {


    long countByHall_HallId(Long hallId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Seat s WHERE s.hall.hallId = :hallId")
    void deleteByHallId(@Param("hallId") Long hallId);

    @Query("SELECT s FROM Seat s WHERE s.hall.hallId = :hallId")
    List<Seat> findByHallId(@Param("hallId") Long hallId);

    @Query("SELECT s FROM Seat s WHERE s.hall.hallId = :hallId AND s.seatNumber IN :seatNumbers")
    List<Seat> lockSeatsByHallAndSeatNumbers(@Param("hallId") Long hallId, @Param("seatNumbers") List<String> seatNumbers);

    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.status = :status WHERE s.seatId = :id")
    int updateSeatStatus(@Param("id") Long id, @Param("status") String status);

    // New method for the simplified approach
    @Query("SELECT s FROM Seat s WHERE s.hall.hallId = :hallId AND s.seatNumber IN :seatNumbers")
    List<Seat> findByHallIdAndSeatNumberIn(@Param("hallId") Long hallId, @Param("seatNumbers") List<String> seatNumbers);
}