package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestTicketDto;
import com.group8.movie_reservation_system.dto.response.ResponseTicketDto;
import com.group8.movie_reservation_system.dto.response.paginate.TicketPaginateResponseDto;
import com.group8.movie_reservation_system.entity.Ticket;
import com.group8.movie_reservation_system.service.TicketService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket-management-service/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    private StandardResponseDto successResponse(Object data) {
        return new StandardResponseDto(200, "Success", data);
    }

    private StandardResponseDto errorResponse() {
        return new StandardResponseDto(401, "Unauthorized: No active session", null);
    }



    @PostMapping("/user/create/")
    public StandardResponseDto createTicket(@RequestBody RequestTicketDto dto, HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");

        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {
            ResponseTicketDto ticket = ticketService.createTicket(dto, loggedUserId);
            return successResponse(ticket);
        }

        return errorResponse();



    }

    @GetMapping("/user/{ticketId}")
    public StandardResponseDto getTicketById(@PathVariable Long ticketId, HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {
            ResponseTicketDto ticket = ticketService.getTicketById(ticketId);
            return successResponse(ticket);
        }

        return errorResponse();


    }

    @GetMapping("/user/{userId}/paged")
    public StandardResponseDto getTicketsByUserPaged(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ResponseTicketDto> pagedTickets = ticketService.getTicketsByUserPaged(userId, pageable);

            TicketPaginateResponseDto response = new TicketPaginateResponseDto();
            response.setDataList(pagedTickets.getContent());
            response.setDataCount(pagedTickets.getTotalElements());

            return successResponse(response);
        }

        return errorResponse();



    }

    @PutMapping("/user/{ticketId}/cancel")
    public StandardResponseDto cancelTicket(@PathVariable String ticketId, HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN") || loggedUserRole.equals("ROLE_USER")) {
            ResponseTicketDto ticket = ticketService.cancelTicket(ticketId);
            return successResponse(ticket);
        }

        return errorResponse();


    }


    @GetMapping("/admin/all/paged")
    public StandardResponseDto getAllTicketsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ResponseTicketDto> pagedTickets = ticketService.getAllTicketsPaged(pageable);

            TicketPaginateResponseDto response = new TicketPaginateResponseDto();
            response.setDataList(pagedTickets.getContent());
            response.setDataCount(pagedTickets.getTotalElements());

            return successResponse(response);
        }

        return errorResponse();



    }

    @GetMapping("/admin/status/{status}")
    public StandardResponseDto getTicketsByStatus(@PathVariable Ticket.TicketStatus status, HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            List<ResponseTicketDto> tickets = ticketService.getTicketsByStatus(status);
            return successResponse(tickets);
        }

        return errorResponse();

    }

    @PutMapping("/admin/{ticketId}/status")
    public StandardResponseDto updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam Ticket.TicketStatus status,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            ResponseTicketDto ticket = ticketService.updateTicketStatus(ticketId, status);
            return successResponse(ticket);
        }

        return errorResponse();

    }

    @PutMapping("/admin/{ticketId}/assign/{adminId}")
    public StandardResponseDto assignTicket(
            @PathVariable Long ticketId,
            @PathVariable String adminId,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            ResponseTicketDto ticket = ticketService.assignTicket(ticketId, adminId);
            return successResponse(ticket);
        }

        return errorResponse();


    }

    @PutMapping("/admin/{ticketId}/priority")
    public StandardResponseDto updateTicketPriority(
            @PathVariable Long ticketId,
            @RequestParam Ticket.TicketPriority priority,
            HttpSession session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            ResponseTicketDto ticket = ticketService.updateTicketPriority(ticketId, priority);
            return successResponse(ticket);
        }

        return errorResponse();


    }

    @PutMapping("/admin/{ticketId}/response")
    public StandardResponseDto addAdminResponse(
            @PathVariable Long ticketId,
            @RequestParam String response,
            HttpSession session
    ) {
        System.out.println(response);
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            ResponseTicketDto ticket = ticketService.addAdminResponse(ticketId, response);
            return successResponse(ticket);
        }

        return errorResponse();


    }

    @DeleteMapping("/admin/{ticketId}")
    public StandardResponseDto deleteTicket(@PathVariable Long ticketId, HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole.equals("ROLE_ADMIN")) {
            ticketService.deleteTicket(ticketId);
            return new StandardResponseDto(200, "Ticket deleted successfully", null);
        }

        return errorResponse();


    }
}
