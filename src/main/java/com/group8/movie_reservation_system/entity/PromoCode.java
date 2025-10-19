package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromoCode {
    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private int code;
    private Date startDate;
    private Date endDate;
    private boolean status;
    private double discount;
    private String description;
    @OneToMany(mappedBy = "promoCode")
    private List<Deal> deal;
}
