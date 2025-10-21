package com.group8.movie_reservation_system.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/promo-code")
public class PromoCodePageController {

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showPromoCodePage(Model model, HttpSession session) {
        String defaultAdminId = "A001";
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        model.addAttribute("adminId", loggedUserId);
        return "promo-code";
    }


    @GetMapping("/page/{adminId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showPromoCodePageWithAdmin(@PathVariable String adminId, Model model, HttpSession session){
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        model.addAttribute("adminId", loggedUserId);
        return "promo-code";
    }


    @GetMapping("/update/{promoCodeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showUpdatePromoCodePage(@PathVariable String promoCodeId, Model model, HttpSession session){
        model.addAttribute("promoCodeId", promoCodeId);
        return "promo-code";
    }


    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showPromoCodeListPage(Model model, HttpSession  session){
        String loggedUserId = (String) session.getAttribute("loggedUserId");
        String defaultAdminId = "A001"; // optional
        model.addAttribute("adminId", loggedUserId);
        return "promo-code";
    }
}
