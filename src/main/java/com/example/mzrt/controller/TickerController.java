package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.service.TickerService;
import com.example.mzrt.service.TickerWithProfitService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tickers")
public class TickerController {

    private final UserService userService;
    private final TickerService tickerService;

    @Autowired
    public TickerController(UserService userService,
                            TickerService tickerService) {
        this.userService = userService;
        this.tickerService = tickerService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String getTickers(@PathVariable int userId,
                                Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("tickers", tickerService.findByUserId(userId));
        return "tickers/list";
    }

    @GetMapping("/{userId}/new")
    public String tickerNewForm(@PathVariable int userId, Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("ticker", Ticker.builder()
                .userId(userId)
                .build());
        return "tickers/new";
    }

    @PostMapping
    public String saveTicker(@ModelAttribute("ticker") Ticker ticker) {
        tickerService.save(ticker);
        return "redirect:/tickers/" + ticker.getUserId();
    }

    @GetMapping("/{id}/updating")
    public String tickerUpdateForm(@PathVariable int id, Model model) {
        Ticker ticker = tickerService.findById(id);
        model.addAttribute("user", userService.findById(ticker.getUserId()));
        model.addAttribute("ticker", ticker);
        return "tickers/update";
    }

    @DeleteMapping("/{id}")
    public String deleteTicker(@PathVariable int id) {
        Ticker ticker = tickerService.findById(id);
        tickerService.deleteById(id);
        return "redirect:/tickers/" + ticker.getUserId();
    }

}
