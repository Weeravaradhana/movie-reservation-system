package com.group8.movie_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group8.movie_reservation_system.entity.Booking;
import com.group8.movie_reservation_system.entity.Hall;
import com.group8.movie_reservation_system.entity.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Showtimes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @OneToMany(mappedBy = "showtime")
    @JsonIgnore
    private List<Booking> bookings = new ArrayList<>();

    private java.math.BigDecimal price;

    // 🔹 Add this version field
    @Version
    @Column(nullable = false)
    private Integer version=0;
}
