package com.group8.moviereservation.movie_reservation_system.service.impl;

import com.group8.moviereservation.movie_reservation_system.dto.request.DealRequestDto;
import com.group8.moviereservation.movie_reservation_system.dto.response.ResponseDealDto;
import com.group8.moviereservation.movie_reservation_system.dto.response.paginate.DealPaginateResponseDto;
import com.group8.moviereservation.movie_reservation_system.entity.Deal;
import com.group8.moviereservation.movie_reservation_system.repo.AdminRepo;
import com.group8.moviereservation.movie_reservation_system.repo.DealRepo;
import com.group8.moviereservation.movie_reservation_system.service.DealService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepo dealRepo;
    private final AdminRepo adminRepo;

    @Override
    public void createDeal(DealRequestDto dto,String id) {
        dealRepo.save(toDeal(dto,id));
    }

    @Override
    public void updateDeal(DealRequestDto dto, String dealId) {
        Deal selectedDeal = dealRepo.findById(dealId).orElseThrow(() -> new EntityNotFoundException("Deal not found"));
        selectedDeal.setCode(dto.getCode());
        selectedDeal.setStartDate(dto.getStartDate());
        selectedDeal.setEndDate(dto.getEndDate());
        selectedDeal.setDiscount(dto.getDiscount());
        selectedDeal.setStatus(dto.isStatus());
        selectedDeal.setDescription(dto.getDescription());
        dealRepo.save(selectedDeal);

    }

    @Override
    public void deleteDeal(String dealId) {
        dealRepo.delete(getDealById(dealId));
    }

    @Override
    public Deal getDealById(String dealId) {
        return dealRepo.findById(dealId).orElseThrow(()-> new EntityNotFoundException("Deal not found!"));
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
                        .build();

    }

    private Deal toDeal(DealRequestDto dto,String id) {
        return dto == null ? null :
                Deal.builder()
                        .id(UUID.randomUUID().toString())
                        .code(dto.getCode())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .description(dto.getDescription())
                        .status(dto.isStatus())
                        .discount(dto.getDiscount())
                        .admin(adminRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Deal not found!")))
                        .build();
    }
}
