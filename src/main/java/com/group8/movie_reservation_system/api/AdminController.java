package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestAdminDto;
import com.group8.movie_reservation_system.dto.response.ResponseAdminDto;
import com.group8.movie_reservation_system.service.AdminService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-management-service/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<StandardResponseDto> createAdmin(
            @RequestBody RequestAdminDto dto,
            HttpSession  session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }


        ResponseAdminDto created = adminService.createAdmin(dto, loggedUserId);
        return new ResponseEntity<>(
                new StandardResponseDto(201, "Admin created successfully", created),
                HttpStatus.CREATED
        );
    }


    @GetMapping
    public ResponseEntity<StandardResponseDto> getAllAdmins(HttpSession  session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }


        List<ResponseAdminDto> allAdmins = adminService.getAllAdmins();
        return new ResponseEntity<>(
                new StandardResponseDto(200, "All admins retrieved successfully", allAdmins),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseDto> getAdminById(@PathVariable String id,HttpSession  session) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }

        return new ResponseEntity<>(
                new StandardResponseDto(200, "Admin retrieved successfully", adminService.getAdminById(id)),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponseDto> updateAdmin(
            @PathVariable String id,
            @RequestBody RequestAdminDto dto,
            HttpSession  session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }



        ResponseAdminDto updated = adminService.updateAdmin(id, dto, loggedUserId);
        return new ResponseEntity<>(
                new StandardResponseDto(200, "Admin updated successfully", updated),
                HttpStatus.OK
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponseDto> deleteAdmin(
            @PathVariable String id,
            HttpSession  session
    ) {

        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        if (loggedUserRole == null) {

            return new ResponseEntity<>(
                    new StandardResponseDto(401, "Unauthorized: No active session", null),
                    HttpStatus.UNAUTHORIZED
            );

        }

        if (!loggedUserRole.equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(
                    new StandardResponseDto(403, "Forbidden: Only admin can create movies", null),
                    HttpStatus.FORBIDDEN
            );
        }

        adminService.deleteAdmin(id, loggedUserId);
        return new ResponseEntity<>(
                new StandardResponseDto(200, "Admin deleted successfully", null),
                HttpStatus.OK
        );
    }
}
