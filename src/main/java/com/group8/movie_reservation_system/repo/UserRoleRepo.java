package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRoleRepo extends JpaRepository<UserRole,String> {

    public Optional<UserRole> findByRoleName(String roleName);
}
