package com.example.mzrt.controller;

import com.example.mzrt.model.StrategyTicker;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/strategy_tickers")
public class StrategyTickerController {
    private final TickerService tickerService;
    private final StrategyTickerService strategyTickerService;
    private final StrategyService strategyService;
    private final DealService dealService;

    @Autowired
    public StrategyTickerController(StrategyService strategyService,
                                    DealService dealService,
                                    StrategyTickerService strategyTickerService,
                                    TickerService tickerService) {
        this.strategyService = strategyService;
        this.dealService = dealService;
        this.strategyTickerService = strategyTickerService;
        this.tickerService = tickerService;
    }

    @GetMapping
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/{strategyId}")
    public String getStrategyTickers(@PathVariable int strategyId, Model model) {
        model.addAttribute("strategy", strategyService.findById(strategyId));
        model.addAttribute("tickers", strategyTickerService.findAllByStrategy(strategyId));
        return "strategy_tickers/list";
    }

    @GetMapping("/{id}/updating")
    public String strategyTickerUpdateForm(@PathVariable int id, Model model) {
        StrategyTicker ticker = strategyTickerService.findById(id);

        model.addAttribute("strategy", strategyService.findById(ticker.getStrategyId()));
        model.addAttribute("ticker", ticker);
        return "strategy_tickers/update";
    }

    @PostMapping
    public String saveStrategyTicker(@ModelAttribute("ticker") StrategyTicker strategyTicker) {

        int strategyId = strategyTicker.getStrategyId();
        String strategyName = strategyService.findById(strategyId).getName();
        Ticker ticker = tickerService.findById(strategyTicker.getTickerId());

        strategyTickerService.save(strategyTicker);
        dealService.updateProfitPriceAtOpenedDeal(strategyName, ticker.getName());
        return "redirect:/strategy_tickers/" + strategyId;
    }
}
