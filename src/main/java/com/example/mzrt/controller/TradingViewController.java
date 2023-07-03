package com.example.mzrt.controller;

import com.example.mzrt.model.Order;
import com.example.mzrt.service.strategies.BlackFlagService;
import com.example.mzrt.service.strategies.MozartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class TradingViewController {

    private final BlackFlagService blackFlagService;
    private final MozartService mozartService;

    public TradingViewController(BlackFlagService blackFlagService, MozartService mozartService) {
        this.blackFlagService = blackFlagService;
        this.mozartService = mozartService;
    }

    /**
     * This method receives the JSON message from the TradingView with the text message inside
     * Handle it accordingly to the strategy.
     * <p>
     * Text can be:
     * 1. Mozart
     * - L1-L5 / S1-S5
     * - LTP1-LTP5 / STP1-STP5
     * - LSL, SSL
     * - Stop trend
     * 2. Black Flag
     * - ~Price crossed above Fib1 level in long trend
     * - Stop Line Change
     *
     * @param token    - Users 'id'
     * @param strategy - Name of strategy
     * @param contract - spot/futures
     * @param ticker   - Pair of the coins like BTCUSDT
     * @param message  - Additional text info
     * @return - Created order if success or empty one if not
     */
    @PostMapping(value = "/{token}/{strategy}/{contract}/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public Order handleAlert(@PathVariable(value = "token") String token,
                             @PathVariable(value = "strategy") String strategy,
                             @PathVariable(value = "contract") String contract,
                             @PathVariable(value = "ticker") String ticker,
                             @RequestBody String message) {
        return switch (strategy) {
            case "bf" -> blackFlagService.handleAlert(token, message, ticker.toUpperCase());
            case "moz" -> mozartService.handleAlert(token, message, ticker.toUpperCase());
            default -> Order.builder().build();
        };
    }

    @PostMapping("/test")
    public Order getTestAlert() {
        return Order.builder()
                .name("test")
                .build();
    }
}
