package com.group8.movie_reservation_system.service;


import com.group8.movie_reservation_system.dto.request.RequestDealtDto;
import com.group8.movie_reservation_system.dto.response.ResponseDealDto;
import com.group8.movie_reservation_system.dto.response.paginate.DealPaginateResponseDto;

public interface DealService {
    public void createDeal(RequestDealtDto dto, String id);
    public void updateDeal(RequestDealtDto dto, String dealId);
    public void deleteDeal(String id);
    public ResponseDealDto getDealById(String dealId);
    public DealPaginateResponseDto getAllDeals(int page, int pageSize, String text);
}
