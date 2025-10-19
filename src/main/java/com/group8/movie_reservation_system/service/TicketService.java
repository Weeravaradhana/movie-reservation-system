package com.group8.movie_reservation_system.service;

import com.group8.movie_reservation_system.dto.request.RequestTicketDto;
import com.group8.movie_reservation_system.dto.response.ResponseTicketDto;
import com.group8.movie_reservation_system.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {

    ResponseTicketDto createTicket(RequestTicketDto dto, String userId);

    ResponseTicketDto getTicketById(Long id);

    List<ResponseTicketDto> getTicketsByUser(String userId);

    Page<ResponseTicketDto> getTicketsByUserPaged(String userId, Pageable pageable);

    List<ResponseTicketDto> getTicketsByStatus(Ticket.TicketStatus status);

    List<ResponseTicketDto> getTicketsByAssignedAdmin(String adminId);

    Page<ResponseTicketDto> getAllTicketsPaged(Pageable pageable);

    ResponseTicketDto updateTicketStatus(Long ticketId, Ticket.TicketStatus status);

    ResponseTicketDto assignTicket(Long ticketId, String adminId);

    ResponseTicketDto updateTicketPriority(Long ticketId, Ticket.TicketPriority priority);

    ResponseTicketDto addAdminResponse(Long ticketId, String response);

    void deleteTicket(Long ticketId);

    long countByStatus(Ticket.TicketStatus status);

    long countByPriorityAndStatusNotClosed(Ticket.TicketPriority priority);

    ResponseTicketDto cancelTicket(String ticketId);
}
