package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestPaymentDto;
import com.group8.movie_reservation_system.entity.Payment;

import java.math.BigDecimal;

public interface PaymentService {

    RequestPaymentDto processPayment(RequestPaymentDto dto);
}
