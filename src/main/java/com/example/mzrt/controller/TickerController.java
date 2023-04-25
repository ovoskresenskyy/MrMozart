package com.example.mzrt.controller;

import com.example.mzrt.model.PercentProfit;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tickers")
public class TickerController {

    private final UserService userService;
    private final StrategyService strategyService;
    private final TickerService tickerService;
    private final TickerWithProfitService tickerWithProfitService;
    private final PercentProfitService percentProfitService;

    @Autowired
    public TickerController(UserService userService,
                            StrategyService strategyService,
                            TickerService tickerService,
                            TickerWithProfitService tickerWithProfitService,
                            PercentProfitService percentProfitService) {
        this.userService = userService;
        this.strategyService = strategyService;
        this.tickerService = tickerService;
        this.tickerWithProfitService = tickerWithProfitService;
        this.percentProfitService = percentProfitService;
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

    @GetMapping("/profits/{userId}/{strategyId}")
    public String getTickersWithProfits(@PathVariable int userId,
                                        @PathVariable int strategyId,
                                        Model model) {
        model.addAttribute("user",
                userService.findById(userId));
        model.addAttribute("strategy",
                strategyService.findById(strategyId));
        model.addAttribute("tickersWithProfits",
                tickerWithProfitService.findAllByUserAndStrategy(userId, strategyId));
        return "tickers/profits";
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
        BinanceDataHolder.getInstance().startPriceTracking(ticker.getName() + "USDT");
        return "redirect:/tickers/" + ticker.getUserId();
    }

    @PostMapping("/profit")
    public String saveTickersProfit(@ModelAttribute("profit") PercentProfit profit) {
        percentProfitService.save(profit);
        return "redirect:/tickers/profits/"
                + tickerService.findById(profit.getTickerId()).getUserId()
                + "/"
                + profit.getStrategyId();
    }

    @GetMapping("/{id}/updating")
    public String tickerUpdateForm(@PathVariable int id, Model model) {
        Ticker ticker = tickerService.findById(id);
        model.addAttribute("user", userService.findById(ticker.getUserId()));
        model.addAttribute("ticker", ticker);
        return "tickers/update";
    }

    @GetMapping("/profit/{tickerId}/{strategyId}/updating")
    public String tickerProfitUpdateForm(@PathVariable int tickerId,
                                         @PathVariable int strategyId,
                                         Model model) {
        Ticker ticker = tickerService.findById(tickerId);
        model.addAttribute("user",
                userService.findById(ticker.getUserId()));
        model.addAttribute("strategy",
                strategyService.findById(strategyId));
        model.addAttribute("ticker",
                ticker);
        model.addAttribute("profit",
                percentProfitService.findByStrategyIdAndTickerId(strategyId, tickerId));
        return "tickers/profits_update";
    }

    @DeleteMapping("/{id}")
    public String deleteTicker(@PathVariable int id) {
        Ticker ticker = tickerService.findById(id);
        tickerService.deleteById(id);

        BinanceDataHolder.getInstance().stopPriceTracking(ticker.getName() + "USDT");
        return "redirect:/tickers/" + ticker.getUserId();
    }

}
