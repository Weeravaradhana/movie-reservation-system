package com.group8.movie_reservation_system.api;


import com.group8.movie_reservation_system.dto.request.RequestPromoCodeDto;
import com.group8.movie_reservation_system.service.PromoCodeService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("deal-management-service/api/v1/promo-code")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping("/admin/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestPromoCodeDto promoCodeRequestDto,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }

        promoCodeService.savePromoCode(promoCodeRequestDto, loggedUserId);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Promo code saved! ", null
                ), HttpStatus.CREATED
        );
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> update(
            @RequestBody RequestPromoCodeDto promoCodeRequestDto, @PathVariable("id") String id,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }

        promoCodeService.updatePromoCode(promoCodeRequestDto, id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Promo code updated! ", null
                ), HttpStatus.CREATED
        );
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String id, HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");

        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }

        promoCodeService.deletePromoCode(id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204, "Promo code deleted! ", null
                ), HttpStatus.NO_CONTENT
        );
    }


    @GetMapping("/admin/find-by-id/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String id, HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200, "Promo code founded! ", promoCodeService.findById(id)
                ), HttpStatus.OK
        );
    }


    @GetMapping("/admin/find-all")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<StandardResponseDto> findAll(
            @RequestParam String searchText,
            @RequestParam int page,
            @RequestParam int size,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }


        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {


            return new ResponseEntity<>(
                    new StandardResponseDto(
                            200, "Promo code list!", promoCodeService.findAll(page, size, searchText)
                    ), HttpStatus.OK
            );



        }

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );




    }
}



