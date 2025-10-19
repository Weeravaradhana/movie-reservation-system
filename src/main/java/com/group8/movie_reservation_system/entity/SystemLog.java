package com.group8.movie_reservation_system.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "SystemLogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;
    private String adminId;
    private String action;
    private LocalDateTime timestamp;
}