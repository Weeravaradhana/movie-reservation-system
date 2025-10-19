package com.group8.movie_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Bookings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    public Long id;


    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    public Showtime showtime;
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Seat> seats = new ArrayList<>();
    @OneToOne(mappedBy = "booking")
    @JsonIgnore
    public Payment payment; // optional until paid
    @Column(name = "status")
    public String status = "PENDING";
    @Column(name = "timestamp")
    public LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}