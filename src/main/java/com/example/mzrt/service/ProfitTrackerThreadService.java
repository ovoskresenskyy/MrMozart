package com.example.mzrt.service;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.model.Deal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ProfitTrackerThreadService implements Runnable {

    private final BinancePriceTracker binancePriceTracker;
    private final int userId;
    private final double profitPrice;
    private final String ticker;
    private final String side;
    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;

    public ProfitTrackerThreadService(BinancePriceTracker binancePriceTracker,
                                      int userId,
                                      double profitPrice,
                                      String ticker,
                                      String side,
                                      OrderService orderService,
                                      AlertService alertService,
                                      DealService dealService) {
        this.binancePriceTracker = binancePriceTracker;
        this.userId = userId;
        this.profitPrice = profitPrice;
        this.ticker = ticker;
        this.side = side;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
    }

    @Override
    public void run() {
        if (side.equals("sell")) shortTakeProfit();
        else if (side.equals("buy")) longTakeProfit();
    }

    private void shortTakeProfit() {
        boolean takeProfit = false;
        while (!takeProfit) takeProfit = binancePriceTracker.getPrice() <= profitPrice;
        sendTakeProfit("STP5",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    private void longTakeProfit() {
        boolean takeProfit = false;
        while (!takeProfit) takeProfit = binancePriceTracker.getPrice() >= profitPrice;
        sendTakeProfit("LTP5",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    private void sendTakeProfit(String alert, String alertTime) {
        Optional<Deal> openedDealByTicker = dealService.getOpenedDealByTicker(
                userId,
                Strategy.BLACK_FLAG.name.toLowerCase(),
                ticker);

        if (openedDealByTicker.isEmpty()) return;

        Deal deal = openedDealByTicker.get();
        orderService.sendClosingOrder(alertService.findByUserIdAndName(
                        userId,
                        alert),
                ticker,
                userId,
                alertTime,
                Strategy.BLACK_FLAG,
                deal.getId());

        deal.setOpen(false);
        dealService.save(deal);
    }
}
