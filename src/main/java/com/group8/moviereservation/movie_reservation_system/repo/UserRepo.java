package com.group8.moviereservation.movie_reservation_system.repo;

import com.group8.moviereservation.movie_reservation_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
}
