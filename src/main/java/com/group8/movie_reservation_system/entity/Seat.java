package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "seats")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;
    private String seatNumber;
    private String type;
    private String category;
    private String layout;
    private String status;
    private int rowNum;
    private int colNum;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public Seat(Hall hall, String seatNumber, String type, String category, String layout,
                String status, int rowNum, int colNum, BigDecimal price) {
        this.hall = hall;
        this.seatNumber = seatNumber;
        this.type = type;
        this.category = category;
        this.layout = layout;
        this.status = status;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.price = price;
    }

}