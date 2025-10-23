package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    private String seatNumber;
    private int rowNum;
    private int colNum;
    private String type;
    private String category;
    private String layout;
    private BigDecimal price;
    private String status;
    @Column(nullable = false)
    private boolean booked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // Convenience constructor
    public Seat(Hall hall, String seatNumber, String type, String category, String layout,
                boolean booked, int rowNum, int colNum, BigDecimal price) {
        this.hall = hall;
        this.seatNumber = seatNumber;
        this.type = type;
        this.category = category;
        this.layout = layout;
        this.booked = booked;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.price = price;
    }
}
