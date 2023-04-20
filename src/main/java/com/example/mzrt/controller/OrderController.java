package com.example.mzrt.controller;

import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.StrategyService;
import com.example.mzrt.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final StrategyService strategyService;

    public OrderController(OrderService orderService,
                           StrategyService strategyService,
                           UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
        this.strategyService = strategyService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}/deal/{dealId}/strategy/{strategyId}")
    public String getOrdersByDeal(@PathVariable int userId,
                                  @PathVariable int dealId,
                                  @PathVariable int strategyId,
                            Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("strategy", strategyService.findById(strategyId));
        model.addAttribute("orders", orderService.findByDealId(dealId));
        return "orders/list";
    }
}
