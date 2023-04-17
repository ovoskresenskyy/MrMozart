package com.example.mzrt.controller;

import com.example.mzrt.service.DealService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/deals")
public class DealController {

    private final UserService userService;
    private final DealService dealService;

    @Autowired
    public DealController(UserService userService, DealService dealService) {
        this.userService = userService;
        this.dealService = dealService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}/{strategy}")
    public String getDeals(@PathVariable int userId,
                           @PathVariable String strategy,
                           Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("deals", dealService.getByUserIdAndStrategy(userId, strategy.toLowerCase()));
        return "deals/list";
    }

}
