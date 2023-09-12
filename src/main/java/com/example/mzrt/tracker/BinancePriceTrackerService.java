package com.example.mzrt.tracker;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.holder.PriceTrackers;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.holder.DealProfitTrackers;
import com.example.mzrt.service.DealService;
import com.example.mzrt.service.TickerService;
import org.springframework.stereotype.Service;

@Service
public class BinancePriceTrackerService implements CryptoConstants {

    private final TickerService tickerService;
    private final DealService dealService;
    private final PriceTrackers priceTrackers;
    private final DealProfitTrackers dealProfitTrackers;

    public BinancePriceTrackerService(TickerService tickerService,
                                      DealService dealService,
                                      DealProfitTrackers dealProfitTrackers,
                                      PriceTrackers priceTrackers) {
        this.tickerService = tickerService;
        this.dealService = dealService;
        this.priceTrackers = priceTrackers;
        this.dealProfitTrackers = dealProfitTrackers;

        startCurrentPriceTracking();
        startProfitTrackers();
    }

    private void startCurrentPriceTracking() {
        for (Ticker ticker : tickerService.findAll()) {
            priceTrackers.startPriceTracking(ticker.getName());
        }
    }

    private void startProfitTrackers() {
        for (Deal deal : dealService.findAllOpened()) {
            if (deal.getStrategyId() == MOZART_STRATEGY_ID) {
                continue;
            }
            dealProfitTrackers.startTracker(deal);
        }
    }
}
