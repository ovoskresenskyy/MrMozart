package com.example.mzrt.controller;

import com.example.mzrt.enums.Strategy;
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

    @Autowired
    public StrategyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getStrategies(@PathVariable int userId,
                                Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("strategies", EnumSet.allOf(Strategy.class)
                .stream()
                .map(s -> s.name)
                .toArray());
        return "strategies/list";
    }

}
