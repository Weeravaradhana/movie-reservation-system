package com.group8.movie_reservation_system.repo;


import com.group8.movie_reservation_system.entity.Ticket;
import com.group8.movie_reservation_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByUser(User user);
    
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    
    List<Ticket> findByAssignedAdmin(User admin);
    
    @Query("SELECT t FROM Ticket t WHERE t.user = :user ORDER BY t.createdAt DESC")
    Page<Ticket> findByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT t FROM Ticket t ORDER BY t.createdAt DESC")
    Page<Ticket> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT t FROM Ticket t WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<Ticket> findByStatusOrderByCreatedAtDesc(@Param("status") Ticket.TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE t.assignedAdmin = :admin ORDER BY t.createdAt DESC")
    List<Ticket> findByAssignedAdminOrderByCreatedAtDesc(@Param("admin") User admin);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") Ticket.TicketStatus status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.priority = :priority AND t.status != 'CLOSED'")
    long countByPriorityAndStatusNotClosed(@Param("priority") Ticket.TicketPriority priority);
    
}
