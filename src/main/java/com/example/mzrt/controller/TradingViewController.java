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
     *
     * @param token    - Users 'id'
     * @param strategy - Name of strategy
     * @param contract - spot/futures
     * @param ticker   - Pair of the coins like BTCUSDT
     * @param message  - Additional text info
     */
    @PostMapping(value = "/{token}/{strategy}/{contract}/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public void handleAlert(@PathVariable(value = "token") String token,
                            @PathVariable(value = "strategy") String strategy,
                            @PathVariable(value = "contract") String contract,
                            @PathVariable(value = "ticker") String ticker,
                            @RequestBody String message) {
        switch (strategy) {
            case "bf" -> blackFlagService.handleAlert(token, message, ticker.toUpperCase());
            case "moz" -> mozartService.handleAlert(token, message, ticker.toUpperCase());
        }
    }

    @PostMapping("/test")
    public Order getTestAlert() {
        return Order.builder()
                .name("test")
                .build();
    }
}
