package com.group8.moviereservation.movie_reservation_system.service;

import com.group8.moviereservation.movie_reservation_system.dto.request.PromoCodeRequestDto;
import com.group8.moviereservation.movie_reservation_system.dto.response.ResponsePromoCode;

import java.util.List;

public interface PromoCodeService {
    public void savePromoCode(PromoCodeRequestDto dto,String id);
    public void updatePromoCode(PromoCodeRequestDto dto,String id);
    public void deletePromoCode(String id);
    public ResponsePromoCode findById(String id);

}
