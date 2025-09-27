package com.group8.moviereservation.movie_reservation_system.repo;

import com.group8.moviereservation.movie_reservation_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking,String> {
}
