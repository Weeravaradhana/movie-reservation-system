package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.Deal;
import com.group8.movie_reservation_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM user",nativeQuery = true)
    public Page<User> searchAllUsers(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM user", nativeQuery = true)
    public long countAllUsers();
}
