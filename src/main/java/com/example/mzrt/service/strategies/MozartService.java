package com.example.mzrt.service.strategies;

import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.*;
import org.springframework.stereotype.Service;

@Service
public class MozartService {

    private final StrategyService strategyService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;

    public MozartService(StrategyService strategyService,
                         OrderService orderService,
                         AlertService alertService,
                         UserService userService) {
        this.strategyService = strategyService;
        this.orderService = orderService;
        this.alertService = alertService;
        this.userService = userService;
    }

    public Order handleAlert(String token, String message, String ticker, String alertTime) {

        int userId = userService.findByToken(token).getId();
        Strategy strategy = strategyService.findById(1);

        if (message.equalsIgnoreCase("Stop Trend")) return sendStopTrend(userId,
                ticker,
                alertTime,
                strategy);
        return orderService.sendOpeningOrder(
                alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        strategy.getId(),
                        message),
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
}
