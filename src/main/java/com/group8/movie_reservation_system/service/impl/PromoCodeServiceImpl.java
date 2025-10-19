package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestPromoCodeDto;
import com.group8.movie_reservation_system.dto.response.ResponsePromoCodeDto;
import com.group8.movie_reservation_system.dto.response.paginate.PromoCodePaginateResponseDto;
import com.group8.movie_reservation_system.entity.Deal;
import com.group8.movie_reservation_system.entity.PromoCode;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.DealRepo;
import com.group8.movie_reservation_system.repo.PromoCodeRepo;
import com.group8.movie_reservation_system.repo.UserRepository;
import com.group8.movie_reservation_system.service.PromoCodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoCodeServiceImpl implements PromoCodeService {
    private final PromoCodeRepo promoCodeRepo;
    private final DealRepo dealRepo;
    private final UserRepository  userRepo;

    @Override
    public void savePromoCode(RequestPromoCodeDto dto, String id) {

        try {
            promoCodeRepo.save(toPromoCode(dto, id));
            dealRepo.save(toDeal(dto, id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updatePromoCode(RequestPromoCodeDto dto, String id) {
        PromoCode selectedPromoCode = promoCodeRepo.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("PromoCode not found"));

        selectedPromoCode.setCode(dto.getCode());
        selectedPromoCode.setDiscount(dto.getDiscount());
        selectedPromoCode.setStartDate(dto.getStartDate());
        selectedPromoCode.setEndDate(dto.getEndDate());
        selectedPromoCode.setStatus(dto.isStatus());
        selectedPromoCode.setDescription(dto.getDescription());
        promoCodeRepo.save(selectedPromoCode);
    }

    @Override
    public void deletePromoCode(String id) {
        PromoCode selectedPromoCode = promoCodeRepo.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("PromoCode not found"));
        System.out.println(selectedPromoCode.getCode());
        promoCodeRepo.deleteById(id);
        dealRepo.deleteByPromoCode(selectedPromoCode.getCode());
    }

    @Override
    public ResponsePromoCodeDto findById(String id) {
        return toResponsePromoCode(promoCodeRepo
                .findById(id).orElseThrow(()->new EntryNotFoundException("PromoCode not found")));
    }

    @Override
    public PromoCodePaginateResponseDto findAll(int page, int size, String text) {
        return PromoCodePaginateResponseDto.builder()
                .dataCount(promoCodeRepo.countAllByPromocodes(text))
                .dataList(
                        promoCodeRepo.searchAllPromocodes(text, PageRequest.of(page,size))
                                .stream().map(this::toResponsePromoCode).collect(Collectors.toList())
                )
                .build();
    }


    private PromoCode toPromoCode(RequestPromoCodeDto dto, String id) throws SQLException {
        return  dto == null ? null:
                PromoCode.builder()
                        .id(UUID.randomUUID().toString())
                        .code(dto.getCode())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .status(dto.isStatus())
                        .description(dto.getDescription())
                        .discount(dto.getDiscount())
                        .build();
    }

    private Deal toDeal(RequestPromoCodeDto dto, String id) throws SQLException {
        return  dto == null ? null:
                Deal.builder()
                        .id(UUID.randomUUID().toString())
                        .code(dto.getCode())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .status(dto.isStatus())
                        .description(dto.getDescription())
                        .discount(dto.getDiscount())
                        .build();
    }







    private ResponsePromoCodeDto toResponsePromoCode(PromoCode promoCode){
        return promoCode == null ? null :
                ResponsePromoCodeDto
                        .builder()
                        .id(promoCode.getId())
                        .code(promoCode.getCode())
                        .status(promoCode.isStatus())
                        .startDate(promoCode.getStartDate())
                        .endDate(promoCode.getEndDate())
                        .discount(promoCode.getDiscount())
                        .description(promoCode.getDescription())

                        .build();
    }



    private List<String> specifyDealId(int code){
        return dealRepo.findDealIdsByCode(code);
    }
}




