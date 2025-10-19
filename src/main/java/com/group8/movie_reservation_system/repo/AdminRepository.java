package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, String> {
    @Query("SELECT a FROM Admin a WHERE a.userName = :userName")
    Optional<Admin> findByUserName(@Param("userName") String userName);

}