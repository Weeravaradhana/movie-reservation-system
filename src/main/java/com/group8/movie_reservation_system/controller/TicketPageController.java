package com.group8.movie_reservation_system.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TicketPageController {

    @GetMapping("/tickets")
    public String ticketsPage(HttpSession session, Model model) {
        String loggedUserRole = (String) session.getAttribute("loggedUserRole");
        String loggedUserId = (String) session.getAttribute("loggedUserId");

        if (loggedUserRole == null) loggedUserRole = "ANONYMOUS";
        if (loggedUserId == null) loggedUserId = "";

        model.addAttribute("loggedUserRole", loggedUserRole);
        model.addAttribute("loggedUserId", loggedUserId);

        return "tickets";
    }
}
