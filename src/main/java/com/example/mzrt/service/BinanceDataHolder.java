package com.example.mzrt.service;

import com.example.mzrt.model.Deal;

import java.util.HashMap;
import java.util.Map;

public class BinanceDataHolder {

    private static BinanceDataHolder instance;

    public final Map<String, BinancePriceTracker> holder = new HashMap<>();
    public final Map<Integer, Thread> profitTreadsHolder = new HashMap<>();
    public final Map<Integer, ProfitTrackerService> profitTrackerHolder = new HashMap<>();

    public static synchronized BinanceDataHolder getInstance() {
        if (instance == null) {
            instance = new BinanceDataHolder();
        }
        return instance;
    }

    public void startPriceTracking(String ticker) {
        if (holder.containsKey(ticker)) return;

        BinancePriceTracker binancePriceProvider = new BinancePriceTracker();
        binancePriceProvider.startTracking(ticker);
        holder.put(ticker, binancePriceProvider);
    }

    public void stopPriceTracking(String ticker) {
        getByTicker(ticker).closeConnection();
        holder.remove(ticker); //TODO: what if few users use one ticker, and one of them decide to delete it? HAHA
    }

    public BinancePriceTracker getByTicker(String ticker) {
        return holder.get(ticker);
    }

    public void startProfitTracker(Deal deal,
                                   OrderService orderService,
                                   AlertService alertService,
                                   DealService dealService,
                                   StrategyService strategyService) {

        if (deal.getTicker() == null) return;
        if (!profitTrackerHolder.containsKey(deal.getId())) {

            ProfitTrackerService profitTrackerService = new ProfitTrackerService(
                    getByTicker(deal.getTicker()),
                    deal,
                    orderService,
                    alertService,
                    dealService,
                    strategyService);
            Thread profitTracker = new Thread(profitTrackerService);
            profitTracker.start();

            profitTrackerHolder.put(deal.getId(), profitTrackerService);
            profitTreadsHolder.put(deal.getId(), profitTracker);
        }
    }

    public void stopProfitTracker(int dealId) {
        if (profitTrackerHolder.containsKey(dealId)) {
            ProfitTrackerService profitTrackerThreadService = profitTrackerHolder.get(dealId);
            profitTrackerThreadService.setKeepTracking(false);
            profitTrackerHolder.remove(dealId);
        }
        if (profitTreadsHolder.containsKey(dealId)) {
            Thread thread = profitTreadsHolder.get(dealId);
            thread.interrupt();
            profitTreadsHolder.remove(dealId);
        }
    }
}
