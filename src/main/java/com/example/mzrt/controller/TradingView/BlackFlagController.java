package com.example.mzrt.controller.TradingView;

import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/bf")
public class BlackFlagController {

    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;
    private final DealService dealService;
    private final StrategyService strategyService;

    @Autowired
    public BlackFlagController(OrderService orderService,
                               AlertService alertService,
                               UserService userService,
                               DealService dealService,
                               StrategyService strategyService) {
        this.orderService = orderService;
        this.alertService = alertService;
        this.userService = userService;
        this.dealService = dealService;
        this.strategyService = strategyService;
    }

    @PostMapping(value = "/{token}/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public Order getAlert(@PathVariable(value = "ticker") String ticker,
                          @PathVariable(value = "token") String token,
                          @RequestBody String message) {

        ticker = ticker.toUpperCase() + "USDT";

        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        int userId = userService.findByToken(token).getId();

        Strategy strategy = strategyService.findById(2);
        if (message.equalsIgnoreCase("Stop Line Change")) return sendStopLoss(userId,
                ticker,
                alertTime,
                strategy);

        String side = getSide(message);
        if (side.equals("")) return Order.builder().build();

        String alertNumber = getAlertNumber(message);
        if (alertNumber.equals("")) return Order.builder().build();

        return orderService.sendOpeningOrder(alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        2,
                        alertNumber + side),
                ticker,
                userId,
                alertTime,
                strategy);
    }

    private Order sendStopLoss(int userId, String ticker, String alertTime, Strategy strategy) {
        Optional<Deal> openedDealByTicker = dealService.getOpenedDealByTicker(
                userId,
                strategy.getName(),
                ticker);

        if (openedDealByTicker.isEmpty()) return Order.builder().build();

        Deal deal = openedDealByTicker.get();
        Order order = orderService.sendClosingOrder(alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        2,
                        deal.getSide().equals("sell") ? "STS" : "STL"),
                ticker,
                userId,
                alertTime,
                strategy,
                deal.getId());

        deal.setOpen(false);
        dealService.save(deal);

        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
        dataHolder.stopProfitTracker(deal.getId());

        return order;
    }

    private String getSide(String message) {
        if (message.indexOf("long trend") > 0) return "L";
        if (message.indexOf("short trend") > 0) return "S";
        return "";
    }

    private String getAlertNumber(String message) {
        int index = message.indexOf("Fib");
        if (index == 0) return "";

        return String.valueOf(message.charAt(index + 3));
    }

    @PostMapping("/test")
    public Order getTestAlert() {
        return Order.builder()
                .name("test")
                .build();
    }
}
