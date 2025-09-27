package com.group8.moviereservation.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromoCode {
    @Id
    private String id;
    private int code;
    private Date startDate;
    private Date endDate;
    private boolean status;
    private double discount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin  admin;
}
