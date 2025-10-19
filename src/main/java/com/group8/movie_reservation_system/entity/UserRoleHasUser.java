package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserRoleHasUser {
    @EmbeddedId
    private UserRoleHasUserKey id= new UserRoleHasUserKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id",nullable = false)
    private UserRole userRole;

    public UserRoleHasUser(User user, UserRole userRole) {
        this.user = user;
        this.userRole = userRole;
    }
}

