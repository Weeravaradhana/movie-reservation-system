package com.group8.movie_reservation_system.service.impl;

import com.group8.movie_reservation_system.dto.request.RequestTicketDto;
import com.group8.movie_reservation_system.dto.response.ResponseTicketDto;
import com.group8.movie_reservation_system.entity.Ticket;
import com.group8.movie_reservation_system.entity.User;
import com.group8.movie_reservation_system.repo.TicketRepository;
import com.group8.movie_reservation_system.repo.UserRepository;
import com.group8.movie_reservation_system.service.TicketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseTicketDto createTicket(RequestTicketDto dto, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority() != null ? dto.getPriority() : Ticket.TicketPriority.MEDIUM);
        ticket.setUser(user);
        ticket.setMovieShowtime(LocalDateTime.now());
        if (dto.getAssignedAdminId() != null) {
            User admin = userRepository.findById(dto.getAssignedAdminId())
                    .orElseThrow(() -> new RuntimeException("Assigned admin not found"));
            ticket.setAssignedAdmin(admin);
            ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        } else {
            ticket.setStatus(Ticket.TicketStatus.OPEN);
        }

        Ticket saved = ticketRepository.save(ticket);
        return toResponseTicketDto(saved);
    }

    @Override
    public ResponseTicketDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return toResponseTicketDto(ticket);
    }

    @Override
    public List<ResponseTicketDto> getTicketsByUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByUser(user).stream()
                .map(this::toResponseTicketDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ResponseTicketDto> getTicketsByUserPaged(String userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::toResponseTicketDto);
    }

    @Override
    public List<ResponseTicketDto> getTicketsByStatus(Ticket.TicketStatus status) {
        return ticketRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::toResponseTicketDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseTicketDto> getTicketsByAssignedAdmin(String adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return ticketRepository.findByAssignedAdminOrderByCreatedAtDesc(admin).stream()
                .map(this::toResponseTicketDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ResponseTicketDto> getAllTicketsPaged(Pageable pageable) {
        return ticketRepository.findAllOrderByCreatedAtDesc(pageable)
                .map(this::toResponseTicketDto);
    }


    @Override
    public ResponseTicketDto updateTicketStatus(Long ticketId, Ticket.TicketStatus status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus(status);
        if (status == Ticket.TicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        return toResponseTicketDto(ticketRepository.save(ticket));
    }

    @Override
    public ResponseTicketDto assignTicket(Long ticketId, String adminId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        ticket.setAssignedAdmin(admin);
        ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        return toResponseTicketDto(ticketRepository.save(ticket));
    }

    @Override
    public ResponseTicketDto updateTicketPriority(Long ticketId, Ticket.TicketPriority priority) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setPriority(priority);
        return toResponseTicketDto(ticketRepository.save(ticket));
    }

    @Override
    public ResponseTicketDto addAdminResponse(Long ticketId, String response) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setAdminResponse(response);
        ticket.setAdminResponseAt(LocalDateTime.now());
        return toResponseTicketDto(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    @Override
    public long countByStatus(Ticket.TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    @Override
    public long countByPriorityAndStatusNotClosed(Ticket.TicketPriority priority) {
        return ticketRepository.countByPriorityAndStatusNotClosed(priority);
    }

    @Override
    public ResponseTicketDto cancelTicket(String ticketId) {
        System.out.println("Hello ticket cancle");
        Long id = Long.parseLong(ticketId);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        LocalDateTime showTime = ticket.getMovieShowtime();
        LocalDateTime now = LocalDateTime.now();

//        if (now.isAfter(showTime.minusHours(2))) {
//            throw new RuntimeException("Ticket cannot be canceled less than 2 hours before show");
//        }

        double refundAmount = calculateRefund(ticket);
        ticket.setRefundAmount(refundAmount);
        ticket.setStatus(Ticket.TicketStatus.CANCELED);
        ticket.setResolvedAt(LocalDateTime.now());

        return toResponseTicketDto(ticketRepository.save(ticket));
    }

    private double calculateRefund(Ticket ticket) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime showTime = ticket.getMovieShowtime();

        long hoursBeforeShow = java.time.Duration.between(now, showTime).toHours();

        if (hoursBeforeShow >= 24) {
            return ticket.getAmountPaid(); // Full refund
        } else if (hoursBeforeShow >= 2) {
            return ticket.getAmountPaid() * 0.5; // Partial 50% refund
        } else {
            return 0.0; // No refund
        }
    }

    private ResponseTicketDto toResponseTicketDto(Ticket ticket) {
        return ResponseTicketDto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .userId(ticket.getUser() != null ? ticket.getUser().getId() : null)
                .username(ticket.getUser() != null ? ticket.getUser().getUsername() : null)
                .assignedAdminId(ticket.getAssignedAdmin() != null ? ticket.getAssignedAdmin().getId() : null)
                .assignedAdminUsername(ticket.getAssignedAdmin() != null ? ticket.getAssignedAdmin().getUsername() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .adminResponse(ticket.getAdminResponse())
                .adminResponseAt(ticket.getAdminResponseAt())
                .refundAmount(ticket.getRefundAmount())
                .build();
    }
}
