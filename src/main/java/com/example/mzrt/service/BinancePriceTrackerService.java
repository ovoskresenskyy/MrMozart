package com.example.mzrt.service;

import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Ticker;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.mzrt.CryptoConstants.MOZART_STRATEGY_ID;

@Service
public class BinancePriceTrackerService {

    private final TickerService tickerService;
    private final AlertService alertService;
    private final OrderService orderService;
    private final DealService dealService;

    public BinancePriceTrackerService(TickerService tickerService,
                                      AlertService alertService,
                                      OrderService orderService,
                                      DealService dealService) {
        this.tickerService = tickerService;
        this.alertService = alertService;
        this.orderService = orderService;
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

            dataHolder.startProfitTracker(deal,
                    orderService,
                    alertService,
                    dealService);
        }
    }
}
