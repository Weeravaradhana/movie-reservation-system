package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestDealtDto;
import com.group8.movie_reservation_system.dto.response.ResponseDealDto;
import com.group8.movie_reservation_system.dto.response.paginate.DealPaginateResponseDto;
import com.group8.movie_reservation_system.entity.Deal;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.DealRepo;
import com.group8.movie_reservation_system.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepo dealRepo;


    @Override
    public void createDeal(RequestDealtDto dto, String adminId) {
        dealRepo.save(toDeal(dto,adminId));
    }

    @Override
    public void updateDeal(RequestDealtDto dto, String dealId) {
        Deal selectedDeal = dealRepo.findById(dealId).orElseThrow(() -> new EntryNotFoundException("Deal not found"));
        selectedDeal.setCode(dto.getCode());
        selectedDeal.setStartDate(dto.getStartDate());
        selectedDeal.setEndDate(dto.getEndDate());
        selectedDeal.setDiscount(dto.getDiscount());
        selectedDeal.setStatus(dto.isStatus());
        selectedDeal.setDescription(dto.getDescription());
        dealRepo.save(selectedDeal);

    }

    @Override
    public void deleteDeal(String id) {
        Deal selectedDeal = dealRepo.findById(id).orElseThrow(() -> new EntryNotFoundException("Deal not found"));
        dealRepo.deleteById(selectedDeal.getId());
    }

    @Override
    public ResponseDealDto getDealById(String dealId) {
        return toResponseDealDto(dealRepo.findById(dealId).
                orElseThrow(()-> new EntryNotFoundException("Deal not found!")));
    }

    @Override
    public DealPaginateResponseDto getAllDeals(int page, int size, String text) {
        return DealPaginateResponseDto.builder()
                .dataCount(dealRepo.countAllDeal(text))
                .dataList(
                        dealRepo.searchAllDeals(text, PageRequest.of(page,size))
                                .stream().map(this::toResponseDealDto).collect(Collectors.toList())
                )
                .build();
    }

    private ResponseDealDto toResponseDealDto(Deal deal){

        return deal == null ? null :
                ResponseDealDto.builder()
                        .dealId(deal.getId())
                        .status(deal.isStatus())
                        .startDate(deal.getStartDate())
                        .endDate(deal.getEndDate())
                        .discount(deal.getDiscount())
                        .code(deal.getCode())
                        .description(deal.getDescription())
                        .build();

    }

    private Deal toDeal(RequestDealtDto dto,String adminId) {
        return dto == null ? null :
                Deal.builder()
                        .id(UUID.randomUUID().toString())
                        .code(dto.getCode())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .description(dto.getDescription())
                        .status(dto.isStatus())
                        .discount(dto.getDiscount())
                        .build();
    }
}
