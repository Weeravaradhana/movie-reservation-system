package com.group8.movie_reservation_system.repo;


import com.group8.movie_reservation_system.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {
    // No custom methods needed for now
}