package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.StrategyService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;
    private final StrategyService strategyService;
    private final UserService userService;

    @Autowired
    public AlertController(AlertService alertService,
                           StrategyService strategyService,
                           UserService userService) {
        this.alertService = alertService;
        this.strategyService = strategyService;
        this.userService = userService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}/{strategyId}")
    public String getAlerts(@PathVariable int userId,
                            @PathVariable int strategyId,
                            Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("strategy", strategyService.findById(strategyId));
        model.addAttribute("alerts", alertService.findByUserAndStrategy(userId, strategyId));
        return "alerts/list";
    }

    @GetMapping("/{userId}/{strategyId}/new")
    public String alertNewForm(@PathVariable int userId,
                               @PathVariable int strategyId,
                               Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("strategy", strategyService.findById(strategyId));
        model.addAttribute("alert", Alert.builder()
                .userId(userId)
                .strategyId(strategyId)
                .build());
        return "alerts/new";
    }

    @PostMapping
    public String saveAlert(@ModelAttribute("alert") Alert alert) {
        alertService.save(alert);
        return "redirect:/alerts/" + alert.getUserId() + "/" + alert.getStrategyId();
    }

    @GetMapping("/updating/{id}")
    public String alertUpdateForm(@PathVariable int id, Model model) {
        Alert alert = alertService.findById(id);
        model.addAttribute("user", userService.findById(alert.getUserId()));
        model.addAttribute("strategy", strategyService.findById(alert.getStrategyId()));
        model.addAttribute("alert", alert);
        return "alerts/update";
    }

    @DeleteMapping("/{id}")
    public String deleteAlert(@PathVariable int id) {
        Alert alert = alertService.findById(id);
        int ownerId = alert.getUserId();
        int strategyId = alert.getStrategyId();
        alertService.deleteById(id);
        return "redirect:/alerts/" + ownerId + "/" + strategyId;
    }
}
