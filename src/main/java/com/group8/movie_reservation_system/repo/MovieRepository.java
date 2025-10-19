package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MovieRepository extends JpaRepository<Movie, Long> {
}