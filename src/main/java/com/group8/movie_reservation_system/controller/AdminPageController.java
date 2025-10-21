package com.group8.movie_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    // Returns the Thymeleaf template located at src/main/resources/templates/admin-management.html.html
    @GetMapping("/admin-management")
    public String adminManagementPage() {
        return "admin-management.html";
    }
}
