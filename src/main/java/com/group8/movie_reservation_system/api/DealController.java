package com.group8.movie_reservation_system.api;


import com.group8.movie_reservation_system.dto.request.RequestDealtDto;
import com.group8.movie_reservation_system.service.DealService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deal-management-service/api/v1/deals")
@RequiredArgsConstructor
public class DealController {

   private final DealService dealService;


    @PostMapping("/admin/create/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")

    public ResponseEntity<StandardResponseDto> create(
            @RequestBody RequestDealtDto dealRequestDto, @PathVariable("id") String id ,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole + " s");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create deals", null),
                    HttpStatus.FORBIDDEN
            );
        }

        dealService.createDeal(dealRequestDto, id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201, "Deal saved!", null
                ), HttpStatus.CREATED
        );

    }


    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<StandardResponseDto> update(
            @RequestBody RequestDealtDto dealRequestDto, @PathVariable("id") String id,
            HttpSession session
    ){

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can update deals", null),
                    HttpStatus.FORBIDDEN
            );
        }

        dealService.updateDeal(dealRequestDto,id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        201,"Deal updated! ",null
                ), HttpStatus.CREATED
        );
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<StandardResponseDto> delete(
            @PathVariable("id") String id,HttpSession session
    ){
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        System.out.println(loggedUserRole);
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can delete deals", null),
                    HttpStatus.FORBIDDEN
            );
        }

        dealService.deleteDeal(id);
        return new ResponseEntity<>(
                new StandardResponseDto(
                        204,"Deal deleted! ",null
                ), HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/visitor/find-by-id/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StandardResponseDto> findById(
            @PathVariable("id") String id

    ){

        return new ResponseEntity<>(
                new StandardResponseDto(
                        200,"Deal founded! ", dealService.getDealById(id)
                ), HttpStatus.OK
        );
    }

    @GetMapping("/visitor/find-all")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StandardResponseDto> findAll(
           @RequestParam String searchText,
           @RequestParam int page,
           @RequestParam int size
    ){
        return new ResponseEntity<>(
                new StandardResponseDto(
                        200,"Deal list!",dealService.getAllDeals(page,size,searchText)
                ), HttpStatus.OK
        );

   }



}
