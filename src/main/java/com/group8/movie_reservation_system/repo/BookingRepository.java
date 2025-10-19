package com.group8.movie_reservation_system.repo;


import com.group8.movie_reservation_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(String userId);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.showtime s " +
            "LEFT JOIN FETCH s.movie " +
            "LEFT JOIN FETCH s.hall " +
            "LEFT JOIN FETCH b.seats bs " +  // ✅ fixed: removed 'bs.seat'
            "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(Long id);
}