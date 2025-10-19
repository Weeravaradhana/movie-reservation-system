package com.group8.movie_reservation_system.dto.request;

import com.group8.movie_reservation_system.entity.Ticket.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestTicketDto {

    private String title;
    private String description;
    private TicketPriority priority;
    private String assignedAdminId;
}
