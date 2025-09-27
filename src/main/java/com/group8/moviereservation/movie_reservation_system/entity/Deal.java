package com.group8.moviereservation.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "deal")
@Builder
public class Deal {
    @Id
    @Column(name = "deal_id",length = 80)
    private String id;
    @Column(name="code", nullable = false, length = 6)
    private int code;
    @Column(name="start_date", nullable = false)
    private Date startDate;
    @Column(name="end_date", nullable = false)
    private Date endDate;
    @Column(name="deal_percentage", nullable = false)
    private double discount;
    @Column(name="description")
    private String description;
    @Column(name="status", nullable = false)
    private boolean status;
    @OneToOne(mappedBy = "deal")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
