package com.group8.moviereservation.movie_reservation_system.service.impl;

import com.group8.moviereservation.movie_reservation_system.dto.request.PromoCodeRequestDto;
import com.group8.moviereservation.movie_reservation_system.dto.response.ResponseAdminDto;
import com.group8.moviereservation.movie_reservation_system.dto.response.ResponseDealDto;
import com.group8.moviereservation.movie_reservation_system.dto.response.ResponsePromoCode;
import com.group8.moviereservation.movie_reservation_system.entity.Admin;
import com.group8.moviereservation.movie_reservation_system.entity.Deal;
import com.group8.moviereservation.movie_reservation_system.entity.PromoCode;
import com.group8.moviereservation.movie_reservation_system.repo.AdminRepo;
import com.group8.moviereservation.movie_reservation_system.repo.DealRepo;
import com.group8.moviereservation.movie_reservation_system.repo.PromoCodeRepo;
import com.group8.moviereservation.movie_reservation_system.service.PromoCodeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoCodeServiceImpl implements PromoCodeService {
    private final PromoCodeRepo promoCodeRepo;
    private final AdminRepo adminRepo;
    private final DealRepo dealRepo;

    @Override
    public void savePromoCode(PromoCodeRequestDto dto,String id) {

        try {
            promoCodeRepo.save(toPromoCode(dto, id));
            dealRepo.save(toDeal(dto, id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updatePromoCode(PromoCodeRequestDto dto, String id) {
        PromoCode selectedPromoCode = promoCodeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PromoCode not found"));

        selectedPromoCode.setCode(dto.getCode());
        selectedPromoCode.setDiscount(dto.getDiscount());
        selectedPromoCode.setStartDate(dto.getStartDate());
        selectedPromoCode.setEndDate(dto.getEndDate());
        selectedPromoCode.setStatus(dto.isStatus());
        promoCodeRepo.save(selectedPromoCode);
    }

    @Override
    public void deletePromoCode(String id) {
        promoCodeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PromoCode not found"));
        promoCodeRepo.deleteById(id);
    }

    @Override
    public ResponsePromoCode findById(String id) {
       return toResponsePromoCode(promoCodeRepo
               .findById(id).orElseThrow(()->new EntityNotFoundException("PromoCode not found")));
    }


    private PromoCode toPromoCode(PromoCodeRequestDto dto, String id) throws SQLException {
        return  dto == null ? null:
                PromoCode.builder()
                        .id(UUID.randomUUID().toString())
                        .code(dto.getCode())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .admin(adminRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Admin Not Found")))
                        .status(dto.isStatus())
                        .discount(dto.getDiscount())
                        .build();
    }

    private Deal toDeal(PromoCodeRequestDto dto, String id) throws SQLException {
        return  dto == null ? null:
                Deal.builder()
                        .id(UUID.randomUUID().toString())
                        .code(dto.getCode())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .admin(adminRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Admin Not Found")))
                        .status(dto.isStatus())
                        .discount(dto.getDiscount())
                        .build();
    }

    public ResponseAdminDto toAdmin(Admin admin,int code) {
        return ResponseAdminDto.builder()
                .adminId(admin.getAdminId())
                .dealId(specifyDealId(code))
                .build();
    }





    private ResponsePromoCode toResponsePromoCode(PromoCode promoCode){
        return promoCode == null ? null :
                ResponsePromoCode
                        .builder()
                        .id(promoCode.getId())
                        .code(promoCode.getCode())
                        .status(promoCode.isStatus())
                        .startDate(promoCode.getStartDate())
                        .endDate(promoCode.getEndDate())
                        .discount(promoCode.getDiscount())
                        .admin(
                               toAdmin(promoCode.getAdmin(), promoCode.getCode())
                        )
                        .build();
    }



    private List<String> specifyDealId(int code){
       return dealRepo.findDealIdsByCode(code);
    }
 }




