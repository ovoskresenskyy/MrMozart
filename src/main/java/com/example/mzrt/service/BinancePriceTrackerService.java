package com.example.mzrt.service;

import com.example.mzrt.model.Ticker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinancePriceTrackerService {

    private final TickerService tickerService;

    public BinancePriceTrackerService(TickerService tickerService) {
        this.tickerService = tickerService;

        startTracking();
    }

    private void startTracking() {
        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();

        List<Ticker> tickers = tickerService.findAll();
        for (Ticker ticker : tickers) {
            dataHolder.startPriceTracking(ticker.getName() + "USDT");
        }
    }
}
