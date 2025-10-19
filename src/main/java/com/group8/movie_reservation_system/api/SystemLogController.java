package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.entity.SystemLog;
import com.group8.movie_reservation_system.service.impl.SystemLogService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/log-management-service/api/v1/logs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService logService;

    private ResponseEntity<StandardResponseDto> successResponse(Object data) {
        return ResponseEntity.ok(new StandardResponseDto(200, "Success", data));
    }

    private ResponseEntity<StandardResponseDto> errorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new StandardResponseDto(status.value(), message, null), status);
    }

    @GetMapping("/admin/get-all")
    public ResponseEntity<StandardResponseDto> getAllLogs(HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ROLE_ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }


        try {
            List<SystemLog> logs = logService.getAllLogs();
            return successResponse(logs);
        } catch (Exception e) {
            return errorResponse("Failed to fetch logs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<StandardResponseDto> deleteLog(@PathVariable Long id,HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ROLE_ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }


        try {
            logService.deleteLog(id);
            return successResponse("Log deleted successfully");
        } catch (Exception e) {
            return errorResponse("Failed to delete log: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/delete-all")
    public ResponseEntity<StandardResponseDto> clearAllLogs(HttpSession session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if(!loggedUserRole.equals("ROLE_ADMIN")) {

            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create promo code", null),
                    HttpStatus.FORBIDDEN
            );
        }


        try {
            logService.clearAllLogs();
            return successResponse("All logs cleared successfully");
        } catch (Exception e) {
            return errorResponse("Failed to clear logs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
