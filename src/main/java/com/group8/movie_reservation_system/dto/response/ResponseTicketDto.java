package com.group8.movie_reservation_system.dto.response;

import com.group8.movie_reservation_system.entity.Ticket.TicketPriority;
import com.group8.movie_reservation_system.entity.Ticket.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTicketDto {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;

    private String userId;
    private String username;
    private String assignedAdminId;
    private String assignedAdminUsername;
    private double refundAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    private String adminResponse;
    private LocalDateTime adminResponseAt;
}
