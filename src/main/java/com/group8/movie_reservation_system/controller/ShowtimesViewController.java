package com.group8.movie_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShowtimesViewController {

    @GetMapping("/showtimes")
    public String showtimesPage() {
        return "showtimes";
    }
}
