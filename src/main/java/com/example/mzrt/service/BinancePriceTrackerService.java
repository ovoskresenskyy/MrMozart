package com.example.mzrt.service;

import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Ticker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinancePriceTrackerService {

    private final TickerService tickerService;
    private final AlertService alertService;
    private final OrderService orderService;
    private final DealService dealService;
    private final StrategyService strategyService;

    public BinancePriceTrackerService(TickerService tickerService,
                                      AlertService alertService,
                                      OrderService orderService,
                                      DealService dealService,
                                      StrategyService strategyService) {
        this.tickerService = tickerService;
        this.alertService = alertService;
        this.orderService = orderService;
        this.dealService = dealService;
        this.strategyService = strategyService;

        startCurrentPriceTracking();
        startProfitTrackers();
    }

    private void startCurrentPriceTracking() {
        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();

        List<Ticker> tickers = tickerService.findAll();
        for (Ticker ticker : tickers) {
            dataHolder.startPriceTracking(ticker.getFullName());
        }
    }

    private void startProfitTrackers() {
        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();

        for (Deal deal : dealService.findAllOpened()) {
            if (deal.getStrategyId() == 1) continue;
            dataHolder.startProfitTracker(deal,
                    orderService,
                    alertService,
                    dealService,
                    strategyService);
        }
    }
}
