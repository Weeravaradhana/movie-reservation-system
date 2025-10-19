package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.Hall;
import com.group8.movie_reservation_system.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {


}