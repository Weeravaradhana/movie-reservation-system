package com.group8.movie_reservation_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Admin {

    @Id
    private String adminId;
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
    private String fullName;

    @NotBlank(message = "Password cannot be empty")
    private String passwordHash;

    @Email(message = "Email must be valid")
    private String userName;

    @Pattern(regexp = "SUPER_ADMIN|STAFF", message = "Role must be SUPER_ADMIN or STAFF")
    private String role;

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
