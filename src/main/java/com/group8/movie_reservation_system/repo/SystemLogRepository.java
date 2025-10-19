package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}