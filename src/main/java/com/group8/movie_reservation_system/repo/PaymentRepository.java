package com.group8.movie_reservation_system.repo;


import com.group8.movie_reservation_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PaymentRepository extends JpaRepository<Payment, Long> {

}