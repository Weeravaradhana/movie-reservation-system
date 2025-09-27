package com.group8.moviereservation.movie_reservation_system.repo;

import com.group8.moviereservation.movie_reservation_system.entity.Deal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DealRepo extends JpaRepository<Deal,String> {

    @Query(value = "SELECT deal_id FROM deal  WHERE code = :code",nativeQuery = true)
    List<String> findDealIdsByCode(@Param("code") int code);

    @Query(value = "SELECT COUNT(*) FROM deal WHERE description LIKE%?1% AND status=true",nativeQuery = true)
    public long countAllDeal(String text);

    @Query(value = "SELECT * FROM deal WHERE status=true ",nativeQuery = true)
    public Page<Deal> searchAllDeals(String text, Pageable pageable);
}
