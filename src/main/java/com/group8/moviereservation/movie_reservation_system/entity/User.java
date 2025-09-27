package com.group8.moviereservation.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "customer")
@Builder
public class User {
    @Id
    @Column(name = "customer_id", length = 80, nullable = false)
    private String customerId;
    @Column(name = "username", length = 100, nullable = false)
    private String Username;
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    @Column(name = "password", length = 100, nullable = false)
    private String password;
    @OneToOne(mappedBy = "user")
    private AppliedDeal appliedDeal;
    @OneToMany(mappedBy = "user")
    private List<Booking>  bookings;
}

