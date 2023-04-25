package com.example.mzrt.controller.TradingView;

import com.example.mzrt.model.Order;
import com.example.mzrt.service.strategies.BlackFlagService;
import com.example.mzrt.service.strategies.MozartService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping
public class TradingViewController {

    private final BlackFlagService blackFlagService;
    private final MozartService mozartService;

    public TradingViewController(BlackFlagService blackFlagService, MozartService mozartService) {
        this.blackFlagService = blackFlagService;
        this.mozartService = mozartService;
    }

    @PostMapping(value = "/{token}/{strategy}/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public Order handleAlert(@PathVariable(value = "token") String token,
                             @PathVariable(value = "strategy") String strategy,
                             @PathVariable(value = "ticker") String ticker,
                             @RequestBody String message) {

        ticker = ticker.toUpperCase() + "USDT";
        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        return switch (strategy) {
            case "bf" -> blackFlagService.handleAlert(token, message, ticker, alertTime);
            case "moz" -> mozartService.handleAlert(token, message, ticker, alertTime);
            default -> Order.builder().build();
        };
    }

}
