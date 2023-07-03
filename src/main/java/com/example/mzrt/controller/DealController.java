package com.example.mzrt.controller;

import com.example.mzrt.model.Deal;
import com.example.mzrt.service.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
    public String getDeals(@PathVariable int dealId, Model model) {

        Deal deal = dealService.findById(dealId);

        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
        deal.setOpen(false);
        deal.setClosingPrice(dataHolder.getFuturesByTicker(deal.getTicker()).getPrice());
        deal.setClosingAlert("Manual");
        dealService.updateLastChangesTime(deal);
        dealService.save(deal);
        dataHolder.stopProfitTracker(deal.getId());

        int userId = deal.getUserId();
        int strategyId = deal.getStrategyId();
        return fillDealsList(userId, strategyId, model);
    }

    @NotNull
    private String fillDealsList(@PathVariable int userId,
                                 @PathVariable int strategyId,
                                 Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("strategy", strategyService.findById(strategyId));
//        model.addAttribute("deals", dealService.getUserDealsByStrategy(userId, strategyId));
        List<Deal> openedDeals = dealService.getByUserIdAndStrategyId(userId, strategyId, true);
        model.addAttribute("openedDeals", openedDeals);
        List<Deal> closedDeals = dealService.getByUserIdAndStrategyId(userId, strategyId, false);
        model.addAttribute("closedDeals", closedDeals);
        return "deals/list";
    }
}
