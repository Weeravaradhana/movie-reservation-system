package com.group8.movie_reservation_system.repository;

import com.group8.movie_reservation_system.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId")
    List<Showtime> findByMovieId(@Param("movieId") Long movieId);
}
