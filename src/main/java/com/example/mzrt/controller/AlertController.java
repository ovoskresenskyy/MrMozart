package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class AlertController {

    private final OrderService orderService;
    private final AlertService alertService;

    @Autowired
    public AlertController(OrderService orderService, AlertService alertService) {
        this.orderService = orderService;
        this.alertService = alertService;
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/list";
    }

    @GetMapping("/alerts/new")
    public String alertNewForm(Model model) {
        model.addAttribute("alert", Alert.builder().build());
        return "alerts/new";
    }

    @GetMapping("/alerts")
    public String getAllAlerts(Model model) {
        model.addAttribute("alerts", alertService.findAll());
        return "alerts/list";
    }

    @PostMapping("/alerts")
    public String saveAlert(@ModelAttribute("alert") Alert alert) {
        alertService.save(alert);
        return "redirect:/alerts";
    }

    @GetMapping("/alerts/{id}/updating")
    public String alertUpdateForm(@PathVariable(value = "id") int id, Model model) {
        Alert alert = alertService.findById(id);
        model.addAttribute("alert", alert);
        return "alerts/update";
    }

    @DeleteMapping("/alerts/{id}")
    public String deleteOwner(@PathVariable(value = "id") int id) {
        alertService.deleteById(id);
        return "redirect:/alerts";
    }

}
