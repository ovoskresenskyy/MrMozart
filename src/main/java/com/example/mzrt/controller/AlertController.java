package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;
    private final StrategyService strategyService;

    @Autowired
    public AlertController(AlertService alertService, StrategyService strategyService) {
        this.alertService = alertService;
        this.strategyService = strategyService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{strategyId}")
    public String getAlerts(@PathVariable int strategyId, Model model) {
        model.addAttribute("strategy", strategyService.findById(strategyId));
        model.addAttribute("alerts", alertService.findByStrategy(strategyId));
        return "alerts/list";
    }

    @GetMapping("/{strategyId}/new")
    public String alertNewForm(@PathVariable int strategyId, Model model) {
        model.addAttribute("strategy", strategyService.findById(strategyId));
        model.addAttribute("alert", Alert.builder()
                .strategyId(strategyId)
                .build());
        return "alerts/new";
    }

    @PostMapping
    public String saveAlert(@ModelAttribute("alert") Alert alert) {
        alertService.save(alert);
        return "redirect:/alerts/" + alert.getStrategyId();
    }

    @GetMapping("/updating/{id}")
    public String alertUpdateForm(@PathVariable int id, Model model) {
        Alert alert = alertService.findById(id);
        model.addAttribute("strategy", strategyService.findById(alert.getStrategyId()));
        model.addAttribute("alert", alert);
        return "alerts/update";
    }

    @DeleteMapping("/{id}")
    public String deleteAlert(@PathVariable int id) {
        Alert alert = alertService.findById(id);
        int strategyId = alert.getStrategyId();
        alertService.deleteById(id);
        return "redirect:/alerts/" + strategyId;
    }
}
