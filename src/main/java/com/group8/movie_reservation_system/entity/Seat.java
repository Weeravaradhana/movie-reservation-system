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

    private String seatNumber;  // e.g., "BX1"
    private int rowNum;         // numeric row
    private int colNum;         // numeric column for layout
    private String type;        // e.g., "Regular", "VIP"
    private String category;    // e.g., "Adult", "Child", "Senior"
    private String layout;      // e.g., "A", "B" row layout
    private BigDecimal price;
    private String status;
    @Column(nullable = false)
    private boolean booked;     // true if booked, false otherwise

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
