package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.Review;
import com.group8.movie_reservation_system.entity.Showtime;
import com.group8.movie_reservation_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByUser(User user);
    
    List<Review> findByStatus(Review.ReviewStatus status);
    
    @Query("SELECT r FROM Review r WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<Review> findByStatusOrderByCreatedAtDesc(@Param("status") Review.ReviewStatus status);
    
    @Query("SELECT r FROM Review r WHERE r.user = :user ORDER BY r.createdAt DESC")
    Page<Review> findByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.status = :status")
    long countByStatus(@Param("status") Review.ReviewStatus status);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.status = 'APPROVED'")
    Double getAverageRating();

    @Query("SELECT s FROM Review s WHERE s.movie.id = :movieId")
    List<Review> findByMovieId(@Param("movieId") Long movieId);
    
}
