package com.example.mzrt.controller;

import com.example.mzrt.holder.DealProfitTrackers;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.DealService;
import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.StrategyService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.example.mzrt.enums.Side.getManualClosingAlertBySide;

@Controller
@RequestMapping("/deals")
public class DealController {

    private final StrategyService strategyService;
    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final DealProfitTrackers dealProfitTrackers;

    @Autowired
    public DealController(StrategyService strategyService,
                          DealService dealService,
                          OrderService orderService,
                          AlertService alertService,
                          DealProfitTrackers dealProfitTrackers) {
        this.strategyService = strategyService;
        this.dealService = dealService;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealProfitTrackers = dealProfitTrackers;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{strategyId}")
    public String getDeals(@PathVariable int strategyId, Model model) {
        return fillDealsList(strategyId, model);
    }

    @GetMapping("/close/{dealId}")
    public String closeDeal(@PathVariable int dealId, Model model) {
        Deal deal = dealService.findById(dealId);
        if (deal.isOpen()) {
            String message = getManualClosingAlertBySide(deal.getSide());
            Alert alert = alertService.getAlert(deal, message);

            orderService.send(deal, alert);
            dealService.closeDeal(deal, "Manual");
            dealProfitTrackers.stopTracker(deal.getId());
        }

        return fillDealsList(deal.getStrategyId(), model);
    }

    @NotNull
    private String fillDealsList(@PathVariable int strategyId, Model model) {
        model.addAttribute("strategy", strategyService.findById(strategyId));
        model.addAttribute("openedDeals", dealService.getDeals(strategyId, true));
        model.addAttribute("closedDeals", dealService.getDeals(strategyId, false));
        return "deals/list";
    }
}
