package com.group8.movie_reservation_system.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serve the admin reviews page (Thymeleaf).
 * Place the HTML template at: src/main/resources/templates/reviews.html
 */
@Controller
@RequiredArgsConstructor
public class ReviewAdminViewController {

    @GetMapping("/admin/reviews")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String reviewsPage(HttpSession session, Model model) {
        // pass session attributes (optional) to template
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        String loggedUserEmail = (String) session.getAttribute("loggedUserEmail");
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");

        model.addAttribute("loggedUserId", loggedUserId);
        model.addAttribute("loggedUserEmail", loggedUserEmail);
        model.addAttribute("loggedUserRole", loggedUserRole);

        return "review";
    }
}
