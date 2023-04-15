package com.example.mzrt.controller;

import com.example.mzrt.service.OrderService;
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

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getOrders(@PathVariable int userId,
                            Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("orders", orderService.findByUserId(userId));
        return "orders/list";
    }

    @GetMapping("/{userId}/{strategy}")
    public String getOrdersByStrategy(@PathVariable int userId,
                                      @PathVariable String strategy,
                            Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("orders", orderService.findByUserIdAndStrategy(userId, strategy.toLowerCase()));
        return "orders/list";
    }

    @DeleteMapping("/{userId}")
    public String deleteAllOrders(@PathVariable int userId) {
        orderService.deleteOrdersByUserId(userId);
        return "redirect:/orders/" + userId;
    }
}
