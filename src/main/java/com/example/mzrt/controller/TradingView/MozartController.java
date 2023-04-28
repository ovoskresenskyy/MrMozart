package com.example.mzrt.controller.TradingView;

import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.StrategyService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/alert")
public class MozartController {

    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;
    private final StrategyService strategyService;

    @Autowired
    public MozartController(OrderService orderService,
                            AlertService alertService,
                            UserService userService,
                            StrategyService strategyService) {
        this.orderService = orderService;
        this.alertService = alertService;
        this.userService = userService;
        this.strategyService = strategyService;
    }

    @PostMapping(value = "/{token}/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public Order getAlert(@PathVariable(value = "ticker") String ticker,
                          @PathVariable(value = "token") String token,
                          @RequestBody String alertText) {

        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        ticker = ticker.toUpperCase();

        int userId = userService.findByToken(token).getId();
        Strategy strategy = strategyService.findById(1);

        if (alertText.equalsIgnoreCase("Stop Trend")) return sendStopTrend(userId,
                ticker,
                alertTime,
                strategy);
        return orderService.sendOpeningOrder(
                alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        strategy.getId(),
                        alertText),
                ticker,
                userId,
                alertTime,
                strategy);
    }

    private Order sendStopTrend(int userId, String ticker, String alertTime, Strategy strategy) {
        orderService.sendOpeningOrder(alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        strategy.getId(),
                        "STS"),
                ticker,
                userId,
                alertTime,
                strategy);
        return orderService.sendOpeningOrder(alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        strategy.getId(),
                        "STL"),
                ticker,
                userId,
                alertTime,
                strategy);
    }

    @PostMapping("/test")
    public Order getTestAlert() {
        return Order.builder()
                .name("test")
                .build();
    }
}
