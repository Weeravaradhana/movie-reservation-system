package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestPaymentDto;
import com.group8.movie_reservation_system.entity.Booking;
import com.group8.movie_reservation_system.entity.Payment;
import com.group8.movie_reservation_system.entity.Seat;
import com.group8.movie_reservation_system.exception.BadRequestException;
import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.MovieRepository;
import com.group8.movie_reservation_system.service.PaymentService;
import com.group8.movie_reservation_system.repo.BookingRepository;
import com.group8.movie_reservation_system.repo.PaymentRepository;
import com.group8.movie_reservation_system.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final SeatService seatService;
    private final MovieRepository movieRepository;

    public RequestPaymentDto processPayment(RequestPaymentDto dto) {
        System.out.println(dto.getBookingId());
//        if (dto.getBookingId() == null)
//            throw new BadRequestException("BookingId is required");
//        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0)
//            throw new BadRequestException("Amount must be positive");
//        if (dto.getMethod() == null || dto.getMethod().isBlank()) dto.setMethod("CARD");
//
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow();


        Payment payment = Payment.builder()
                .cardHolderName(dto.getCardHolderName())
                .cardNumber(dto.getCardNumber())
                .expiryDate(dto.getExpiryDate())
                .booking(booking)
                .amount(dto.getAmount())
                .method(dto.getMethod())
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);


        List<Seat> seats = booking.getSeats();
        for (Seat seat : seats) {
            seatService.bookSeatWithDeal(seat.getSeatId(), booking.getShowtime().getId(),dto.getPromoCode());
        }


        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        return toPaymentRequestDto(payment);
    }

    @Override
    public List<RequestPaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::toPaymentRequestDto).collect(Collectors.toList());
    }

    private RequestPaymentDto toPaymentRequestDto(Payment payment) {
        if (payment == null) return null;

        return RequestPaymentDto.builder()
                .expiryDate(payment.getExpiryDate())
                .cardNumber(payment.getCardNumber())
                .cardHolderName(payment.getCardHolderName())
                .bookingId(payment.getBooking() != null ? payment.getBooking().getId() : null)
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .build();
    }

}
