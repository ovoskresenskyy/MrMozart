package com.example.mzrt.controller;

import com.example.mzrt.model.Deal;
import com.example.mzrt.service.BinanceDataHolder;
import com.example.mzrt.service.DealService;
import com.example.mzrt.service.StrategyService;
import com.example.mzrt.service.UserService;
import org.jetbrains.annotations.NotNull;
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
    private final StrategyService strategyService;
    private final DealService dealService;

    @Autowired
    public DealController(UserService userService,
                          StrategyService strategyService,
                          DealService dealService) {
        this.userService = userService;
        this.strategyService = strategyService;
        this.dealService = dealService;
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

        dealService.closeDeal(deal, "Manual");

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
