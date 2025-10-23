package com.group8.movie_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentPageController {

    @GetMapping("/booking/payment")
    public String paymentPage(
            @RequestParam("showtimeId") Long showtimeId,
            @RequestParam("movieId") Long movieId,
            Model model) {

        System.out.println("Payment page called with bookingId: "
                + ", showtimeId: " + showtimeId
                + ", movieId: " + movieId);

        model.addAttribute("showtimeId", showtimeId);
        model.addAttribute("movieId", movieId);

        return "booking/payment";
    }
}
