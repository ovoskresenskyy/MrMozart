package com.example.mzrt.service;

import com.example.mzrt.model.Deal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProfitTrackerThreadService implements Runnable {

    private final BinancePriceTracker binancePriceTracker;
    private final Deal deal;
    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final StrategyService strategyService;
    private boolean keepTracking;

    public ProfitTrackerThreadService(BinancePriceTracker binancePriceTracker,
                                      Deal deal,
                                      OrderService orderService,
                                      AlertService alertService,
                                      DealService dealService,
                                      StrategyService strategyService) {
        this.binancePriceTracker = binancePriceTracker;
        this.deal = deal;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
        this.strategyService = strategyService;
        this.keepTracking = true;
    }

    @Override
    public void run() {
        String side = deal.getSide();
        if (side.equals("sell")) shortTakeProfit();
        else if (side.equals("buy")) longTakeProfit();
    }

    private void shortTakeProfit() {
        boolean takeProfit = false;
        double currentPrice = 0;
        while (!takeProfit && keepTracking) {
            currentPrice = binancePriceTracker.getPrice();
//            double profitPrice = deal.getProfitPrice();
//
//            System.out.println(deal.getTicker()
//                    + " Short. Current price ("
//                    + price
//                    + ") > profit price ("
//                    + profitPrice
//                    + ")");
//            takeProfit = price <= profitPrice;
            takeProfit = currentPrice <= deal.getProfitPrice();
        }
        sendTakeProfit("STP5", currentPrice);
    }

    private void longTakeProfit() {
        boolean takeProfit = false;
        double currentPrice = 0;
        while (!takeProfit && keepTracking) {
            currentPrice = binancePriceTracker.getPrice();
//            double profitPrice = deal.getProfitPrice();
//
//            System.out.println(deal.getTicker()
//                    + " Long. Current price ("
//                    + price
//                    + ") < profit price ("
//                    + profitPrice
//                    + ")");
//            takeProfit = price >= profitPrice;
            takeProfit = currentPrice >= deal.getProfitPrice();
        }
        sendTakeProfit("LTP5", currentPrice);
    }

    private void sendTakeProfit(String alert, double currentPrice) {

        orderService.sendClosingOrder(alertService.findByUserIdAndStrategyIdAndName(
                        deal.getUserId(),
                        deal.getStrategyId(),
                        alert),
                deal.getTicker(),
                deal.getUserId(),
                LocalDateTime
                        .now()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                strategyService.findById(deal.getStrategyId()),
                deal.getId(),
                currentPrice);

        deal.setOpen(false);
        deal.setClosingPrice(currentPrice);
        dealService.save(deal);

        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
        dataHolder.stopProfitTracker(deal.getId());
    }

    public void setKeepTracking(boolean keepTracking) {
        this.keepTracking = keepTracking;
    }
}
