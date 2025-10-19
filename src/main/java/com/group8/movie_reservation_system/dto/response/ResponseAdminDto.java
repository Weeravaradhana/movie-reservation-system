package com.group8.movie_reservation_system.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAdminDto {
    private String adminId;
    private String fullName;
    private String username;
    private String role;
    private String status;
    private String userId;
}
