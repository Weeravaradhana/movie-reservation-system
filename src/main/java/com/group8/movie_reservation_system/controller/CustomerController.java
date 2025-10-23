package com.group8.movie_reservation_system.controller;

import com.group8.movie_reservation_system.dto.response.ResponseBookingDto;
import com.group8.movie_reservation_system.dto.response.ResponseUserDto;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8001";

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model, HttpSession session) {
        // Check if user is logged in
        String userId = (String) session.getAttribute("loggedUserId");
        System.out.println("userId = Bro" + userId);
        if (userId == null) {
            return "redirect:/login?error=Please login to access dashboard";
        }

        try {

            String userUrl = BASE_URL + "/user-management-service/api/v1/users/" + userId;
            ResponseEntity<ResponseUserDto> userResponse = restTemplate.getForEntity(userUrl, ResponseUserDto.class);
            
            if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                model.addAttribute("user", userResponse.getBody());
            }

            // Fetch user bookings
            String bookingUrl = BASE_URL + "/booking-management-service/api/v1/bookings/user/" + userId;
            ResponseEntity<List> bookingResponse = restTemplate.getForEntity(bookingUrl, List.class);
            
            if (bookingResponse.getStatusCode().is2xxSuccessful() && bookingResponse.getBody() != null) {
                model.addAttribute("bookings", bookingResponse.getBody());
            } else {
                model.addAttribute("bookings", List.of());
            }

            // Fetch booking history
            String historyUrl = BASE_URL + "/booking-management-service/api/v1/bookings/user/" + userId + "/history";
            ResponseEntity<List> historyResponse = restTemplate.getForEntity(historyUrl, List.class);
            
            if (historyResponse.getStatusCode().is2xxSuccessful() && historyResponse.getBody() != null) {
                model.addAttribute("bookingHistory", historyResponse.getBody());
            } else {
                model.addAttribute("bookingHistory", List.of());
            }

        } catch (Exception e) {
            model.addAttribute("error", "Unable to load dashboard data");
            model.addAttribute("bookings", List.of());
            model.addAttribute("bookingHistory", List.of());
        }

        model.addAttribute("session", session);
        return "customer/dashboard";
    }

    @PostMapping("/customer/profile/update")
    public String updateProfile(@RequestParam String fullName,
                               @RequestParam String role,
                               @RequestParam String username,
                               HttpSession session,
                               Model model) {
        String userId = (String) session.getAttribute("loggedUserId");
        if (userId == null) {
            return "redirect:/login?error=Please login to update profile";
        }

        try {
            // Create user update request
            ResponseUserDto userDto = new ResponseUserDto();
            userDto.setUsername(username);
            userDto.setUsername(fullName);
            userDto.setRole(role);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ResponseUserDto> request = new HttpEntity<>(userDto, headers);

            // Call REST API
            String url = BASE_URL + "/user-management-service/api/v1/users/" + userId;
            ResponseEntity<StandardResponseDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, StandardResponseDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                model.addAttribute("success", "Profile updated successfully");
            } else {
                model.addAttribute("error", "Failed to update profile");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile. Please try again.");
        }

        return "redirect:/customer/dashboard";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loggedUserId");
        if (userId == null) {
            return "redirect:/login?error=Please login to cancel booking";
        }

        try {
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);

            // Call REST API
            String url = BASE_URL + "/booking-management-service/api/v1/bookings/cancel/" + id;
            ResponseEntity<StandardResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, request, StandardResponseDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                StandardResponseDto responseDto = response.getBody();
                if (responseDto.getCode() == 200) {
                    model.addAttribute("success", "Booking cancelled successfully");
                } else {
                    model.addAttribute("error", "Failed to cancel booking: " + responseDto.getMessage());
                }
            } else {
                model.addAttribute("error", "Failed to cancel booking");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to cancel booking. Please try again.");
        }

        return "redirect:/customer/dashboard";
    }

    @GetMapping("/bookings/{id}")
    public String viewBooking(@PathVariable Long id, Model model, HttpSession session) {
        String userId = (String) session.getAttribute("loggedUserId");
        if (userId == null) {
            return "redirect:/login?error=Please login to view booking";
        }

        try {
            // Fetch booking details
            String url = BASE_URL + "/booking-management-service/api/v1/bookings/" + id;
            ResponseEntity<ResponseBookingDto> response = restTemplate.getForEntity(url, ResponseBookingDto.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ResponseBookingDto booking = response.getBody();
                model.addAttribute("booking", booking);
            } else {
                model.addAttribute("error", "Booking not found");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load booking details");
            return "error";
        }

        model.addAttribute("session", session);
        return "booking/movie-booking";
    }
}

