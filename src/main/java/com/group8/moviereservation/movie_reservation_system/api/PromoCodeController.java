package com.group8.moviereservation.movie_reservation_system.api;

import com.group8.moviereservation.movie_reservation_system.dto.request.PromoCodeRequestDto;
import com.group8.moviereservation.movie_reservation_system.service.PromoCodeService;
import com.group8.moviereservation.movie_reservation_system.util.StandardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deal-management-service/api/v1/promo-code")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping("/admin/create/{id}")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody PromoCodeRequestDto promoCodeRequestDto,@PathVariable("id") String id
            ){
        promoCodeService.savePromoCode(promoCodeRequestDto,id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201,"Promo code saved! ",null
                ), HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @RequestBody PromoCodeRequestDto promoCodeRequestDto,@PathVariable("id") String id
    ){
        promoCodeService.updatePromoCode(promoCodeRequestDto,id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201,"Promo code updated! ",null
                ), HttpStatus.CREATED
        );
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<StandardResponseDto> delete(
           @PathVariable("id") String id
    ){
        promoCodeService.deletePromoCode(id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204,"Promo code deleted! ",null
                ), HttpStatus.NO_CONTENT
        );
    }


    @GetMapping("/admin/find-by-id/{id}")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String id
    ){

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200,"Promo code founded! ", promoCodeService.findById(id)
                ), HttpStatus.OK
        );
    }


}
