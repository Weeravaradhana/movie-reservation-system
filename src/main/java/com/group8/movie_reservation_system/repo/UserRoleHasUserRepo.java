package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.UserRoleHasUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface UserRoleHasUserRepo extends JpaRepository<UserRoleHasUser,String> {
}
