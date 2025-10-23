package com.group8.movie_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;
    @Column(name = "card_number")
    private long cardNumber;
    @Column(name = "expiry_date")
    private Date expiryDate;
    @Column(name = "card_holder_name")
    private String cardHolderName;
    @ManyToOne()
    @JoinColumn(name = "booking_id",nullable = false)
    private Booking booking;

    private BigDecimal amount;
    private String method; // CARD, CASH, UPI
    private String status; // INITIATED, SUCCESS, FAILED, REFUNDED

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}