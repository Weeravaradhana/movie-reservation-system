package com.group8.moviereservation.movie_reservation_system.repo;

import com.group8.moviereservation.movie_reservation_system.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Admin,String> {

}
