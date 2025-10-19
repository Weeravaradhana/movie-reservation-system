package com.group8.movie_reservation_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRoleHasUserKey implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "role_id")
    private String roleId;
}