package com.example.mzrt.service.strategies;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.mzrt.CryptoConstants.*;
import static com.example.mzrt.enums.AlertMessage.isStopTrend;

@Service
public class MozartService {

    private final StrategyService strategyService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;
    private final DealService dealService;

    public MozartService(StrategyService strategyService,
                         OrderService orderService,
                         AlertService alertService,
                         UserService userService,
                         DealService dealService) {
        this.strategyService = strategyService;
        this.orderService = orderService;
        this.alertService = alertService;
        this.userService = userService;
        this.dealService = dealService;
    }

    public Order handleAlert(String token, String message, String ticker) {
        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        int userId = userService.findByToken(token).getId();
        Strategy strategy = strategyService.findById(MOZART_STRATEGY_ID);

        if (isStopTrend(message)) {
            return sendStopTrend(userId, ticker, alertTime, strategy);
        }

        Alert alert = alertService.findByUserIdAndStrategyIdAndName(userId, MOZART_STRATEGY_ID, message);
        Deal deal = dealService.findById(MOZART_DEAL_ID);
        Order order = orderService.placeOrder(deal, alert, alertTime, ticker);
        dealService.updateLastChangesTime(deal);
        return order;
    }

    private Order sendStopTrend(int userId, String ticker, String alertTime, Strategy strategy) {
        Alert sts = alertService.findByUserIdAndStrategyIdAndName(userId, MOZART_STRATEGY_ID, "STS");
        Alert stl = alertService.findByUserIdAndStrategyIdAndName(userId, MOZART_STRATEGY_ID, "STL");
        Deal deal = dealService.findById(MOZART_DEAL_ID);

        orderService.placeOrder(deal, sts, alertTime, ticker);
        Order orderSTL = orderService.placeOrder(deal, stl, alertTime, ticker);

        dealService.updateLastChangesTime(deal);

        return orderSTL;
    }
}
