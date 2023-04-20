package com.example.mzrt.controller;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.service.StrategyService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;

@Controller
@RequestMapping("/strategies")
public class StrategyController {

    private final UserService userService;
    private final StrategyService strategyService;

    @Autowired
    public StrategyController(UserService userService,
                              StrategyService strategyService) {
        this.userService = userService;
        this.strategyService = strategyService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getStrategies(@PathVariable int userId,
                                Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("strategies", strategyService.findAll());
        return "strategies/list";
    }

}
