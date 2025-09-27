package com.group8.moviereservation.movie_reservation_system.service;

import com.group8.moviereservation.movie_reservation_system.dto.request.DealRequestDto;
import com.group8.moviereservation.movie_reservation_system.dto.response.paginate.DealPaginateResponseDto;
import com.group8.moviereservation.movie_reservation_system.entity.Deal;



public interface DealService {
    public void createDeal(DealRequestDto dto,String id);
    public void updateDeal(DealRequestDto dto, String dealId);
    public void deleteDeal(String dealId);
    public Deal getDealById(String dealId);
    public DealPaginateResponseDto getAllDeals(int page, int pageSize, String text);
}
