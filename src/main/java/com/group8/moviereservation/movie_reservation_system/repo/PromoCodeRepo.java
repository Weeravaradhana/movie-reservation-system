package com.group8.moviereservation.movie_reservation_system.repo;

import com.group8.moviereservation.movie_reservation_system.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeRepo extends JpaRepository<PromoCode, String> {
}
