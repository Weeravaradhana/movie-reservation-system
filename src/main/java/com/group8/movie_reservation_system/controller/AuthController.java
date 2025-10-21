package com.group8.movie_reservation_system.controller;

import com.group8.movie_reservation_system.dto.request.RequestUserDto;
import com.group8.movie_reservation_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

private final UserService userService;
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new RequestUserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(
            @ModelAttribute("user") RequestUserDto userDto,
            Model model
    ) {
        try {
            userService.signup(userDto);
            model.addAttribute("success", true);
            return "signup"; // success message show in same page
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "redirect:/login?logout=true";
    }

    @GetMapping("/deal")
    public String indexPage() {
        return "deal";
    }
}





