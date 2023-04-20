package com.example.mzrt.controller;

import com.example.mzrt.service.TickerWithProfitService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tickers")
public class TickerController {

    private final UserService userService;
    private final TickerWithProfitService tickerWithProfitService;

    @Autowired
    public TickerController(UserService userService,
                            TickerWithProfitService tickerWithProfitService) {
        this.userService = userService;
        this.tickerWithProfitService = tickerWithProfitService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getTickers(@PathVariable int userId,
                                Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("tickers", tickerWithProfitService.findAllByUserId(userId));
        return "tickers/list";
    }

}
