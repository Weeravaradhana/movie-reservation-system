package com.group8.movie_reservation_system.service.impl;
import com.group8.movie_reservation_system.entity.SystemLog;
import com.group8.movie_reservation_system.repo.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SystemLogService {

    private final SystemLogRepository logRepository;

    public void logAction(String adminId, String action) {
        SystemLog log = new SystemLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
    public List<SystemLog> getAllLogs() {
        return logRepository.findAll();
    }
    public void deleteLog(Long id) {
        logRepository.deleteById(id);
    }

    public void clearAllLogs() {
        logRepository.deleteAll();
    }
}