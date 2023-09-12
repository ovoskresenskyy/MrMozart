package com.example.mzrt.controller;

import com.example.mzrt.holder.PriceTrackers;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.service.TickerService;
import com.example.mzrt.service.TickerWithCurrentPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tickers")
public class TickerController {

    private final TickerService tickerService;
    private final TickerWithCurrentPriceService tickerWithCurrentPriceService;
    private final PriceTrackers priceTrackers;

    @Autowired
    public TickerController(TickerService tickerService,
                            TickerWithCurrentPriceService tickerWithCurrentPriceService,
                            PriceTrackers priceTrackers) {
        this.tickerService = tickerService;
        this.tickerWithCurrentPriceService = tickerWithCurrentPriceService;
        this.priceTrackers = priceTrackers;
    }

    @GetMapping
    public String getTickers(Model model) {
        model.addAttribute("tickersAndPrices", tickerWithCurrentPriceService.findAll());
        return "tickers/list";
    }

    @GetMapping("/new")
    public String tickerNewForm(Model model) {
        model.addAttribute("ticker", Ticker.builder().build());
        return "tickers/new";
    }

    @PostMapping
    public String saveTicker(@ModelAttribute("ticker") Ticker ticker) {
        ticker.setName(ticker.getName().toUpperCase());
        tickerService.save(ticker);
        priceTrackers.startPriceTracking(ticker.getName());
        return "redirect:/tickers";
    }

    @GetMapping("/{id}/updating")
    public String tickerUpdateForm(@PathVariable int id, Model model) {
        Ticker ticker = tickerService.findById(id);
        model.addAttribute("ticker", ticker);
        return "tickers/update";
    }

    @DeleteMapping("/{id}")
    public String deleteTicker(@PathVariable int id) {
        Ticker ticker = tickerService.findById(id);
        tickerService.deleteById(id);
        priceTrackers.stopPriceTracking(ticker.getName());
        return "redirect:/tickers";
    }
}
