package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.service.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.example.mzrt.enums.Side.*;

@Controller
@RequestMapping("/deals")
public class DealController {

    private final UserService userService;
    private final StrategyService strategyService;
    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;

    @Autowired
    public DealController(UserService userService,
                          StrategyService strategyService,
                          DealService dealService,
                          OrderService orderService,
                          AlertService alertService) {
        this.userService = userService;
        this.strategyService = strategyService;
        this.dealService = dealService;
        this.orderService = orderService;
        this.alertService = alertService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}/{strategyId}")
    public String getDeals(@PathVariable int userId,
                           @PathVariable int strategyId,
                           Model model) {
        return fillDealsList(userId, strategyId, model);
    }

    @GetMapping("/close/{dealId}")
    public String closeDeal(@PathVariable int dealId, Model model) {
        Deal deal = dealService.findById(dealId);
        if (deal.isOpen()) {
            String message = getManualClosingAlertBySide(deal.getSide());
            Alert alert = alertService.getAlert(deal, message);

            orderService.sendClosingOrder(deal, alert);
            dealService.closeDeal(deal, "Manual");
        }

        return fillDealsList(deal.getUserId(), deal.getStrategyId(), model);
    }

    @NotNull
    private String fillDealsList(@PathVariable int userId,
                                 @PathVariable int strategyId,
                                 Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("strategy", strategyService.findById(strategyId));

        model.addAttribute("openedDeals", dealService.getDeals(userId, strategyId, true));
        model.addAttribute("closedDeals", dealService.getDeals(userId, strategyId, false));

        return "deals/list";
    }
}
