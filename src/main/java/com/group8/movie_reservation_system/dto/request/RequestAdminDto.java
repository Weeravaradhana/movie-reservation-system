package com.group8.movie_reservation_system.dto.request;

import com.group8.movie_reservation_system.entity.Admin;
import com.group8.movie_reservation_system.entity.User;
import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestAdminDto {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
    private String fullName;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Email(message = "Email must be valid")
    private String username;

    @Pattern(regexp = "SUPER_ADMIN|STAFF", message = "Role must be SUPER_ADMIN or STAFF")
    private String role;

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status;

}
