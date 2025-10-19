package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestPaymentDto;
import com.group8.movie_reservation_system.service.PaymentService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-management-service/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StandardResponseDto> processPayment(
            @RequestBody RequestPaymentDto paymentRequest,
            HttpSession session
    ) {
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        if (loggedUserId == null) {
            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );
        }


        RequestPaymentDto response = paymentService.processPayment(paymentRequest);

        return new ResponseEntity<>(
                new StandardResponseDto(200, "Payment processed successfully", response),
                HttpStatus.OK
        );
    }

}
