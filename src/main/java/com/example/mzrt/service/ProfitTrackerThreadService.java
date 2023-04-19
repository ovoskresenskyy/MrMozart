package com.example.mzrt.service;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.model.Deal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ProfitTrackerThreadService implements Runnable {

    private final BinancePriceTracker binancePriceTracker;
    private final Deal deal;
    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;

    public ProfitTrackerThreadService(BinancePriceTracker binancePriceTracker,
                                      Deal deal,
                                      OrderService orderService,
                                      AlertService alertService,
                                      DealService dealService) {
        this.binancePriceTracker = binancePriceTracker;
        this.deal = deal;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
    }

    @Override
    public void run() {
        String side = deal.getSide();
        if (side.equals("sell")) shortTakeProfit();
        else if (side.equals("buy")) longTakeProfit();
    }

    private void shortTakeProfit() {
        boolean takeProfit = false;
        while (!takeProfit) takeProfit = binancePriceTracker.getPrice() <= deal.getProfitPrice();
        sendTakeProfit("STP5",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    private void longTakeProfit() {
        boolean takeProfit = false;
        while (!takeProfit) takeProfit = binancePriceTracker.getPrice() >= deal.getProfitPrice();
        sendTakeProfit("LTP5",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    private void sendTakeProfit(String alert, String alertTime) {
        Optional<Deal> openedDealByTicker = dealService.getOpenedDealByTicker(
                deal.getUserId(),
                Strategy.BLACK_FLAG.name.toLowerCase(),
                deal.getTicker());

        if (openedDealByTicker.isEmpty()) return;

        Deal deal = openedDealByTicker.get();
        orderService.sendClosingOrder(alertService.findByUserIdAndName(
                        deal.getUserId(),
                        alert),
                deal.getTicker(),
                deal.getUserId(),
                alertTime,
                Strategy.BLACK_FLAG,
                deal.getId());

        deal.setOpen(false);
        dealService.save(deal);
    }
}
