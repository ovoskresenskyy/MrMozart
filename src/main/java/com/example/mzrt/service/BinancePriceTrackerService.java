package com.example.mzrt.service;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Ticker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinancePriceTrackerService implements CryptoConstants {

    private final TickerService tickerService;
    private final DealService dealService;

    public BinancePriceTrackerService(TickerService tickerService,
                                      DealService dealService) {
        this.tickerService = tickerService;
        this.dealService = dealService;

        startCurrentPriceTracking();
        startProfitTrackers();
    }

    private void startCurrentPriceTracking() {
        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();

        List<Ticker> tickers = tickerService.findAll();
        for (Ticker ticker : tickers) {
            dataHolder.startPriceTracking(ticker.getName());
        }
    }

    private void startProfitTrackers() {
        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();

        for (Deal deal : dealService.findAllOpened()) {
            if (deal.getStrategyId() == MOZART_STRATEGY_ID) {
                continue;
            }

            dataHolder.startProfitTracker(deal);
        }
    }
}
