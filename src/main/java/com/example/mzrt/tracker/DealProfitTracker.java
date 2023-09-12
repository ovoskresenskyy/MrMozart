package com.example.mzrt.tracker;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.holder.DealProfitTrackers;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.StrategyTicker;
import com.example.mzrt.service.*;

import static com.example.mzrt.enums.AlertMessage.*;
import static com.example.mzrt.enums.Side.isShort;

public class DealProfitTracker implements Runnable, CryptoConstants {

    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final DealProfitTrackers dealProfitTrackers;
    private FuturesPriceTracker binancePriceTracker;
    private Deal deal;
    private StrategyTicker strategyTicker;
    private boolean keepTracking;

    public DealProfitTracker(DealService dealService,
                             OrderService orderService,
                             AlertService alertService,
                             DealProfitTrackers dealProfitTrackers) {
        this.dealService = dealService;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealProfitTrackers = dealProfitTrackers;

        this.keepTracking = true;
    }

    /**
     * This method init the new instance.
     * According to the side it will start the different methods
     */
    @Override
    public void run() {
        boolean aShort = isShort(deal.getSide());
        boolean takeProfit = false;
        boolean stopLoss = false;

        while (!takeProfit && !stopLoss && keepTracking) {
            pause();

            double currentPrice = binancePriceTracker.getPrice();

            /* Sometimes happens that there are no deals on the binance,
             * so we can't get current price.
             *
             * Have to wait until it's appears. */
            if (currentPrice == 0) {
                continue;
            }

            Deal updatedDeal = dealService.findById(deal.getId());

            double profitPrice = updatedDeal.getProfitPrice();
            double averagePrice = updatedDeal.getAveragePrice();
            takeProfit = aShort ? currentPrice <= profitPrice : currentPrice >= profitPrice;

            boolean isTakeProfitTaken = updatedDeal.getTakePrice1() > 0;
            if (strategyTicker.isStopWhenUsed() && isTakeProfitTaken) {
                stopLoss = aShort ? currentPrice > averagePrice : currentPrice < averagePrice;
            }
        }

        if (stopLoss) {
            sendStopLoss(aShort);
            return;
        }

        if (takeProfit) {
            sendTakeProfit();
        }

    }

    private void sendTakeProfit() {
        AlertMessage message = OrderPriceService.getNextTP(deal);

        boolean isOrderSent = orderService.send(deal, alertService.findByDealAndName(deal, message.getName()));
        if (isOrderSent) {
            dealService.updatePricesByAlert(deal, message);
        }

        if (message == STP5 || message == LTP5) {
            dealService.closeDeal(deal, message.getName());
            dealProfitTrackers.stopTracker(deal.getId());
        } else {
            this.run();
        }
    }

    private void sendStopLoss(boolean aShort) {
        String message = aShort ? SSL.getName() : LSL.getName();
        orderService.send(deal, alertService.getAlert(deal, message));
        dealService.closeDeal(deal, message);
        dealProfitTrackers.stopTracker(deal.getId());
    }

    public void setKeepTracking(boolean keepTracking) {
        this.keepTracking = keepTracking;
    }

    /**
     * This method is a wrapper for a Thread.sleep
     */
    private void pause() {
        try {
            Thread.sleep(PROFIT_PRICE_SEARCHING_PAUSE_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStrategyTicker(StrategyTicker strategyTicker) {
        this.strategyTicker = strategyTicker;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    public void setBinancePriceTracker(FuturesPriceTracker binancePriceTracker) {
        this.binancePriceTracker = binancePriceTracker;
    }
}
