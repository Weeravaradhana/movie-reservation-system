package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "halls")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hallId;
    private String name;
    private int capacity;
    private String type;

    public Hall(String name, int capacity, String type) {
        this.name = name;
        this.capacity = capacity;
        this.type = type;
    }


    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;


}