package com.example.mzrt.service.strategies;

import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BlackFlagService {

    private final StrategyService strategyService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final DealService dealService;
    private final UserService userService;

    @Autowired
    public BlackFlagService(StrategyService strategyService,
                            OrderService orderService,
                            AlertService alertService,
                            DealService dealService,
                            UserService userService) {
        this.strategyService = strategyService;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
        this.userService = userService;
    }

    public Order handleAlert(String token, String message, String ticker, String alertTime) {

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

        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
        double currentPrice = dataHolder.getByTicker(ticker).getPrice();

        Order order = orderService.sendClosingOrder(alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        2,
                        deal.getSide().equals("sell") ? "STS" : "STL"),
                ticker,
                userId,
                alertTime,
                strategy,
                deal.getId(),
                currentPrice);

        deal.setOpen(false);
        deal.setClosingPrice(currentPrice);
        dealService.save(deal);

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
}
