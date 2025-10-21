package com.group8.movie_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingPageController {

    // GET request example: /booking/movie/seats?movieId=1
    @GetMapping("/booking/movie/seats")
    public String seatPage(@RequestParam("movieId") Long movieId, Model model) {
        System.out.println("seatPage called with movieId: " + movieId);
        model.addAttribute("movieId", movieId);
        return "booking/seats"; // maps to templates/booking/seats.html
    }

}
