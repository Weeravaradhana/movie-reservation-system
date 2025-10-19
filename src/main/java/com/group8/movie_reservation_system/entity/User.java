package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "[user]")
public class User {
    @Id
    private String id;
    private String fullName;
    @Column(unique = true)
    private String username;
    private String passwordHash;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRoleHasUser> userRoleHasUsers;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
    @OneToOne(mappedBy = "user")
    private Admin admin;

    public Set<UserRole> getRoles() {
        return userRoleHasUsers.stream()
                .map(UserRoleHasUser::getUserRole)
                .collect(java.util.stream.Collectors.toSet());
    }
}
