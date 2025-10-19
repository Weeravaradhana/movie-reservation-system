package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.Deal;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface DealRepo extends JpaRepository<Deal,String> {

    @Query(value = "SELECT deal_id FROM deal  WHERE code = :code",nativeQuery = true)
    List<String> findDealIdsByCode(@Param("code") int code);

    @Query(value = "SELECT COUNT(*) FROM deal WHERE description LIKE CONCAT('%', ?1, '%') AND status = 1", nativeQuery = true)
    public long countAllDeal(String text);

    @Query(value = "SELECT * FROM deal WHERE status=1 ",nativeQuery = true)
    public Page<Deal> searchAllDeals(String text, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE  FROM deal WHERE code=:code",nativeQuery = true)
    public void deleteByPromoCode(int code);

    Optional<Deal> findDealsByCode(int promoCode);
}
