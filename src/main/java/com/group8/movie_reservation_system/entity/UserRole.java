package com.group8.movie_reservation_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    @Id
    private String id;

    public UserRole(String id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    @Column(name = "role_name")
    private String roleName;

    @OneToMany(mappedBy = "userRole")
    private Set<UserRoleHasUser> userRoleHasUsers;
}