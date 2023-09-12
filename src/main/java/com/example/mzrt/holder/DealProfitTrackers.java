package com.example.mzrt.holder;

import com.example.mzrt.model.Deal;
import com.example.mzrt.model.StrategyTicker;
import com.example.mzrt.service.DealService;
import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.StrategyTickerService;
import com.example.mzrt.tracker.DealProfitTracker;
import com.example.mzrt.tracker.FuturesPriceTracker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DealProfitTrackers {

    public final Map<Integer, DealProfitTracker> profitTrackerHolder = new HashMap<>();

    private final PriceTrackers priceTrackers;
    private final StrategyTickerService strategyTickerService;
    private final DealService dealService;
    private final OrderService orderService;

    public DealProfitTrackers(PriceTrackers priceTrackers,
                              StrategyTickerService strategyTickerService,
                              DealService dealService,
                              OrderService orderService) {
        this.priceTrackers = priceTrackers;
        this.strategyTickerService = strategyTickerService;
        this.dealService = dealService;
        this.orderService = orderService;
    }

    public void startTracker(Deal deal) {
        if (!profitTrackerHolder.containsKey(deal.getId())) {
            FuturesPriceTracker futuresPriceTracker = priceTrackers.getFuturesTracker(deal.getTicker());
            DealProfitTracker profitTracker = initProfitTracker(deal, futuresPriceTracker);
            new Thread(profitTracker).start();
            profitTrackerHolder.put(deal.getId(), profitTracker);
        }
    }

    private DealProfitTracker initProfitTracker(Deal deal, FuturesPriceTracker priceTracker) {
        String ticker = deal.getTicker();
        int strategyId = deal.getStrategyId();

        StrategyTicker strategyTicker = strategyTickerService.findByTickerNameAndStrategyId(ticker, strategyId);

        DealProfitTracker dealProfitTracker = new DealProfitTracker(dealService,
                orderService,
                this);
        dealProfitTracker.setDeal(deal);
        dealProfitTracker.setStrategyTicker(strategyTicker);
        dealProfitTracker.setBinancePriceTracker(priceTracker);

        return dealProfitTracker;
    }
    public void stopTracker(int dealId) {
        if (profitTrackerHolder.containsKey(dealId)) {
            DealProfitTracker profitTrackerThreadService = profitTrackerHolder.get(dealId);
            profitTrackerThreadService.setKeepTracking(false);
            profitTrackerHolder.remove(dealId);
        }
    }
}
