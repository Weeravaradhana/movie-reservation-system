package com.group8.movie_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentPageController {

    // GET request example: /booking/payment?movieId=1
    @GetMapping("/booking/payment")
    public String paymentPage(@RequestParam("movieId") Long movieId, Model model) {
        System.out.println("paymentPage called with movieId: " + movieId);
        model.addAttribute("movieId", movieId);
        return "booking/payment"; // templates/booking/payment.html
    }

}
