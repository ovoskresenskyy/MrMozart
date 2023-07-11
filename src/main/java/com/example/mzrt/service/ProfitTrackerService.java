package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.service.binance.BinanceFuturesPriceTracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.mzrt.enums.Side.isShort;

public class ProfitTrackerService implements Runnable {

    private final BinanceFuturesPriceTracker binancePriceTracker;
    private final Deal deal;
    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;
    private boolean keepTracking;

    //TODO: too many parameters
    public ProfitTrackerService(BinanceFuturesPriceTracker binancePriceTracker,
                                Deal deal,
                                OrderService orderService,
                                AlertService alertService,
                                DealService dealService) {
        this.binancePriceTracker = binancePriceTracker;
        this.deal = deal;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
        this.keepTracking = true;
    }

    /**
     * This method init the new instance.
     * According to the side it will start the different methods
     */
    @Override
    public void run() {
        boolean aShort = isShort(deal.getSide());

        if (aShort) {
            shortTakeProfit();
        } else {
            longTakeProfit();
        }
    }

    private void shortTakeProfit() {
        boolean takeProfit = false;
        double currentPrice = 0;
        while (!takeProfit && keepTracking) {
            currentPrice = binancePriceTracker.getPrice();
            if (currentPrice == 0) continue;
            double profitPrice = dealService.findById(deal.getId()).getProfitPrice();
            takeProfit = currentPrice <= profitPrice;
        }
        if (takeProfit) sendTakeProfit("STP5", currentPrice);
    }

    private void longTakeProfit() {
        boolean takeProfit = false;
        double currentPrice = 0;
        while (!takeProfit && keepTracking) {
            currentPrice = binancePriceTracker.getPrice();
            double profitPrice = dealService.findById(deal.getId()).getProfitPrice();
            if (profitPrice == 0) continue;
            takeProfit = currentPrice >= profitPrice;
        }
        if (takeProfit) sendTakeProfit("LTP5", currentPrice);
    }

    private void sendTakeProfit(String alertName, double currentPrice) {
        Alert alert = alertService.findByUserIdAndStrategyIdAndName(deal.getUserId(), deal.getStrategyId(), alertName);
        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        orderService.placeOrder(deal, alert, alertTime);

        //TODO: close only if TP5
        dealService.closeDeal(deal, currentPrice, alertName);

        BinanceDataHolder.getInstance().stopProfitTracker(deal.getId());
    }

    public void setKeepTracking(boolean keepTracking) {
        this.keepTracking = keepTracking;
    }
}
