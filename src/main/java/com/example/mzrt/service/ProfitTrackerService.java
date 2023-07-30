package com.example.mzrt.service;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.service.binance.BinanceFuturesPriceTracker;

import static com.example.mzrt.enums.AlertMessage.LTP5;
import static com.example.mzrt.enums.Side.isShort;

public class ProfitTrackerService implements Runnable, CryptoConstants {

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
        double currentPrice;
        while (!takeProfit && keepTracking) {
            pause(DEFAULT_PAUSE_TIME);

            currentPrice = binancePriceTracker.getPrice();
            if (currentPrice == 0) {
                continue;
            }

            takeProfit = currentPrice <= deal.getProfitPrice();
        }
        if (takeProfit) sendTakeProfit(AlertMessage.STP5.getName());
    }

    private void longTakeProfit() {
        boolean takeProfit = false;
        double currentPrice;
        while (!takeProfit && keepTracking) {
            pause(DEFAULT_PAUSE_TIME);

            currentPrice = binancePriceTracker.getPrice();
            if (currentPrice == 0) {
                continue;
            }

            takeProfit = currentPrice >= deal.getProfitPrice();
        }
        if (takeProfit) sendTakeProfit(LTP5.getName());
    }

    private void sendTakeProfit(String alertName) {
        Alert alert = alertService.findByUserIdAndStrategyIdAndName(deal.getUserId(), deal.getStrategyId(), alertName);
        orderService.send(deal, alert);

        //TODO: have to close only if TP5
        dealService.closeDeal(deal, alertName);
    }

    public void setKeepTracking(boolean keepTracking) {
        this.keepTracking = keepTracking;
    }

    /**
     * This method is a wrapper for a Thread.sleep
     *
     * @param time - Time in ms, how long current thread will sleep
     */
    private void pause(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
