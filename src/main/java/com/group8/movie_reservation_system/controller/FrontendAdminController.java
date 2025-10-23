package com.group8.movie_reservation_system.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FrontendAdminController {

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {

        String userRole = (String) session.getAttribute("loggedUserRole");
        if (!"ROLE_ADMIN".equals(userRole)) {
            return "redirect:/login?error=Admin access required";
        }


        model.addAttribute("session", session);
        return "admin/dashboard";
    }

    @GetMapping("/admin/movies")
    public String showMovieList(HttpSession session, Model model) {
        System.out.println(session.getAttribute("loggedUserRole"));
        String userRole = (String) session.getAttribute("loggedUserRole");
        if (!"ROLE_ADMIN".equals(userRole)) {
            return "redirect:/login?error=Admin access required";
        }

        model.addAttribute("session", session);
        return "movies"; // templates/admin/movies.html
    }


    @GetMapping("/admin/movies/add")
    public String showAddMoviePage(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("loggedUserRole");
        if (!"ROLE_ADMIN".equals(userRole)) {
            return "redirect:/login?error=Admin access required";
        }

        model.addAttribute("session", session);
         return "redirect:/movie";
    }

    @GetMapping("/admin/movies/edit")
    public String showEditMoviePage(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("loggedUserRole");
        if (!"ROLE_ADMIN".equals(userRole)) {
            return "redirect:/login?error=Admin access required";
        }

        model.addAttribute("session", session);
        return "admin/movie-edit";
    }


    @GetMapping("/admin/users")
    public String showUserManagementPage(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("loggedUserRole");
        if (!"ROLE_ADMIN".equals(userRole)) {
            return "redirect:/login?error=Admin access required";
        }

        model.addAttribute("session", session);
        return "admin/dashboard";
    }


    @GetMapping("/admin/bookings")
    public String showBookingsPage(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("loggedUserRole");
        if (!"ROLE_ADMIN".equals(userRole)) {
            return "redirect:/login?error=Admin access required";
        }

        model.addAttribute("session", session);
        return "booking/movie-booking";
    }

}

