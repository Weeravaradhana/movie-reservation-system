package com.group8.moviereservation.movie_reservation_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Admin {
    @Id
    private String adminId;
    private String password;
    private String username;
    private String email;
    @OneToMany(mappedBy = "admin")
    private List<Deal> deal;
    @OneToMany(mappedBy = "admin")
    private List<PromoCode>  promoCode;
}
