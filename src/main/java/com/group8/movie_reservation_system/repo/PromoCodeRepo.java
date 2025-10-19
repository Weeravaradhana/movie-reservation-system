package com.group8.movie_reservation_system.repo;

import com.group8.movie_reservation_system.entity.PromoCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PromoCodeRepo extends JpaRepository<PromoCode, String> {
    @Query(value = "SELECT COUNT(*) FROM promo_code WHERE code LIKE CONCAT('%', ?1, '%')", nativeQuery = true)
    public long countAllByPromocodes(String code);

    @Query(value = "SELECT * FROM promo_code",nativeQuery = true)
    public Page<PromoCode> searchAllPromocodes(String text, Pageable pageable);
}
