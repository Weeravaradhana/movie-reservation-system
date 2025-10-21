package com.group8.movie_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/deals")
public class DealWebController {


    @GetMapping("/create")
    public String showCreateDealPage(Model model){
        String defaultAdminId = "A001";
        model.addAttribute("adminId", defaultAdminId);
        return "deal";
    }

    @GetMapping("/create/{adminId}")
    public String showCreateDealPageWithPath(@PathVariable String adminId, Model model){
        model.addAttribute("adminId", adminId);
        return "deal";
    }

    @GetMapping("/list")
    public String showDealListPage(){
        return "deal";
    }

    @GetMapping("/update/{dealId}")
    public String showUpdateDealPage(@PathVariable String dealId, Model model){
        model.addAttribute("dealId", dealId);
        return "deal";
    }
}
