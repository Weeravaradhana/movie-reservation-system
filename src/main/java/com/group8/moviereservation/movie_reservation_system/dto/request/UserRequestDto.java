package com.group8.moviereservation.movie_reservation_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    @NotBlank @NotBlank(message = "Username is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(max = 6, message = "Password must be at least 6 characters")
    private String password;
    @NotBlank(message = "Email is required")
    @Email(message = "Email formate is not valid")
    private String email;
}
