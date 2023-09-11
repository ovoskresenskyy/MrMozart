package com.example.mzrt.tracker;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.StrategyTicker;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.service.*;
import com.example.mzrt.service.binance.BinanceFuturesPriceTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.example.mzrt.enums.AlertMessage.*;
import static com.example.mzrt.enums.Side.isShort;

@Component
public class ProfitTrackerService implements Runnable, CryptoConstants {

    private BinanceFuturesPriceTracker binancePriceTracker;
    private Deal deal;
    private DealService dealService;
    private OrderService orderService;
    private AlertService alertService;
    private TickerService tickerService;
    private StrategyTickerService strategyTickerService;
    private boolean keepTracking;

    public void setBinancePriceTracker(BinanceFuturesPriceTracker binancePriceTracker) {
        this.binancePriceTracker = binancePriceTracker;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }

    @Autowired
    public void setTickerService(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    @Autowired
    public void setStrategyTickerService(StrategyTickerService strategyTickerService) {
        this.strategyTickerService = strategyTickerService;
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
        Ticker ticker = tickerService.findByName(deal.getTicker());
        StrategyTicker strategyTicker = strategyTickerService.findByTickerAndStrategyId(ticker, deal.getStrategyId());

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
            boolean isTakeProfitTaken = updatedDeal.getTakePrice1() > 0;

            takeProfit = aShort ? currentPrice <= profitPrice : currentPrice >= profitPrice;

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
        } else {
            this.run();
        }
    }

    private void sendStopLoss(boolean aShort) {
        String message = aShort ? SSL.getName() : LSL.getName();
        orderService.send(deal, alertService.getAlert(deal, message));
        dealService.closeDeal(deal, message);
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
