package com.group8.movie_reservation_system.api;

import com.group8.movie_reservation_system.dto.request.RequestLoginDto;
import com.group8.movie_reservation_system.dto.request.RequestUserDto;
import com.group8.movie_reservation_system.dto.response.ResponseUserDto;
import com.group8.movie_reservation_system.entity.User;
import com.group8.movie_reservation_system.service.UserService;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-management-service/api/v1/users")
@RequiredArgsConstructor
public class AuthRestController {


    private final UserService userService;

    @PostMapping("/visitors/signup")
    public ResponseEntity<StandardResponseDto> signup(@RequestBody RequestUserDto userDto) {
        System.out.println("signup");
         userService.signup(userDto);
         return new ResponseEntity<>(
                 new StandardResponseDto(201,"User registered successfully",null),
                 HttpStatus.CREATED
         );

    }

    @PostMapping("/visitors/login")
    public ResponseEntity<StandardResponseDto> login(@RequestBody RequestLoginDto dto, HttpSession session) {
        ResponseUserDto responseUserDto = userService.findByUsername(dto.getUsername());


        userService.login(dto);
        session.setAttribute("loggedUserId",responseUserDto.getId());
        session.setAttribute("loggedUserRole",responseUserDto.getRole());
        session.setAttribute("loggedUserEmail",responseUserDto.getUsername());
        return new ResponseEntity<>(
                new StandardResponseDto(200,"User login successfully",null),
                HttpStatus.OK
        );

    }

    @PostMapping("/visitors/logout")
    public ResponseEntity<StandardResponseDto> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>(
                new StandardResponseDto(200,"User logout successfully",null),
                HttpStatus.OK
        );

    }


    @GetMapping("/visitors/check")
    public Response checkSession(HttpSession session) {
        String userId = (String) session.getAttribute("loggedUserId");
        String role = (String) session.getAttribute("loggedUserRole");

        if(userId != null) {
            return new Response(true, "Session active for User ID: " + userId + ", Role: " + role);
        } else {
            return new Response(false, "No active session");
        }
    }


    @Getter
    public static class Response {
        private final boolean success;
        private final String message;

        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

    }
}
