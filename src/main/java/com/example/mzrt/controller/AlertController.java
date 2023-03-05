package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;
    private final UserService userService;

    @Autowired
    public AlertController(AlertService alertService, UserService userService) {
        this.alertService = alertService;
        this.userService = userService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getAlerts(@PathVariable int userId,
                            Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("alerts", alertService.findByUserId(userId));
        return "alerts/list";
    }

    @GetMapping("/{userId}/new")
    public String alertNewForm(@PathVariable int userId,
            Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("alert", Alert.builder()
                .userId(userId)
                .build());
        return "alerts/new";
    }

    @PostMapping
    public String saveAlert(@ModelAttribute("alert") Alert alert) {
        alertService.save(alert);
        return "redirect:/alerts/" + alert.getUserId();
    }

    @GetMapping("/updating/{id}")
    public String alertUpdateForm(@PathVariable int id, Model model) {
        Alert alert = alertService.findById(id);
        model.addAttribute("user", userService.findById(alert.getUserId()));
        model.addAttribute("alert", alert);
        return "alerts/update";
    }

    @DeleteMapping("/{id}")
    public String deleteOwner(@PathVariable int id) {
        int ownerId = alertService.findById(id).getUserId();
        alertService.deleteById(id);
        return "redirect:/alerts/" + ownerId;
    }
}
