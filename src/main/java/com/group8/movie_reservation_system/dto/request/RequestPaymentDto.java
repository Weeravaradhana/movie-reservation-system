package com.group8.movie_reservation_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPaymentDto {
    private Long bookingId;
    private BigDecimal amount;
    private String method;
    private int promoCode;
    private Long cardNumber;
    private Date expiryDate;
    private String cardHolderName;


    private String userId;
    private Long showtimeId;
    private List<Long> seatIds;
}
