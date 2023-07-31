package com.example.mzrt.service;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.model.Deal;
import com.example.mzrt.service.binance.BinanceFuturesPriceTracker;

import static com.example.mzrt.enums.AlertMessage.LTP5;
import static com.example.mzrt.enums.AlertMessage.STP5;
import static com.example.mzrt.enums.Side.isShort;

public class ProfitTrackerService implements Runnable, CryptoConstants {

    private final BinanceFuturesPriceTracker binancePriceTracker;
    private final Deal deal;
    private final DealService dealService;
    private final OrderService orderService;
    private final AlertService alertService;
    private boolean keepTracking;

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
        takeProfitDetermining();
    }

    private void takeProfitDetermining() {
        boolean aShort = isShort(deal.getSide());
        boolean takeProfit = false;

        while (!takeProfit && keepTracking) {
            pause();

            double currentPrice = binancePriceTracker.getPrice();

            /* Sometimes happens that there are no deals on the binance,
             * so we can't get current price.
             *
             * Have to wait until it's appears. */
            if (currentPrice == 0) {
                continue;
            }

            takeProfit = aShort
                    ? currentPrice <= deal.getProfitPrice()
                    : currentPrice >= deal.getProfitPrice();
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
        } else {
            this.run();
        }
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
}
