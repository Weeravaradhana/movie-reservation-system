package com.group8.movie_reservation_system.service;


import com.group8.movie_reservation_system.dto.request.RequestPromoCodeDto;
import com.group8.movie_reservation_system.dto.response.ResponsePromoCodeDto;
import com.group8.movie_reservation_system.dto.response.paginate.PromoCodePaginateResponseDto;

public interface PromoCodeService {
    public void savePromoCode(RequestPromoCodeDto dto, String id);
    public void updatePromoCode(RequestPromoCodeDto dto,String id);
    public void deletePromoCode(String id);
    public ResponsePromoCodeDto findById(String id);
    public PromoCodePaginateResponseDto findAll(int page, int size, String text);

}
