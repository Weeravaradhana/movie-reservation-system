package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(
        name = "seat_allocations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_seat_showtime", columnNames = {"seat_id", "showtime_id"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeatAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "showtime_id")
    private Long showtimeId;
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
    @Column(name="price")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id")
    private Deal deal;

    public SeatAllocation(Seat seat, Long showtimeId, String status) {
        this.seat = seat;
        this.showtimeId = showtimeId;
        this.status = status;
    }

}