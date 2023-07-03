package com.example.mzrt.service;

import com.example.mzrt.model.Deal;
import com.example.mzrt.service.binance.BinanceFuturesPriceTracker;
import com.example.mzrt.service.binance.BinanceSpotPriceTracker;

import java.util.HashMap;
import java.util.Map;

public class BinanceDataHolder {

    private static BinanceDataHolder instance;
    public final Map<String, BinanceFuturesPriceTracker> futuresPriceHolder = new HashMap<>();
    public final Map<String, BinanceSpotPriceTracker> spotPriceHolder = new HashMap<>();
    public final Map<Integer, Thread> profitTreadsHolder = new HashMap<>();
    public final Map<Integer, ProfitTrackerService> profitTrackerHolder = new HashMap<>();

    public static synchronized BinanceDataHolder getInstance() {
        if (instance == null) instance = new BinanceDataHolder();
        return instance;
    }

    public void startPriceTracking(String ticker) {
        startFuturesTracking(ticker);
//        startSpotTracking(ticker);
    }

    private void startFuturesTracking(String ticker){
        if (futuresPriceHolder.containsKey(ticker)) return;
        BinanceFuturesPriceTracker futuresPriceTracker = new BinanceFuturesPriceTracker();
        futuresPriceTracker.startTracking(ticker);
        futuresPriceHolder.put(ticker, futuresPriceTracker);
    }

    private void startSpotTracking(String ticker){
        if (spotPriceHolder.containsKey(ticker)) return;
        BinanceSpotPriceTracker spotPriceTracker = new BinanceSpotPriceTracker();
        spotPriceTracker.startTracking(ticker);
        spotPriceHolder.put(ticker, spotPriceTracker);
    }

    public void stopPriceTracking(String ticker) {
        getFuturesByTicker(ticker).closeConnection();
        futuresPriceHolder.remove(ticker); //TODO: what if few users use one ticker, and one of them decide to delete it? HAHA

//        getSpotByTicker(ticker).closeConnection();
//        spotPriceHolder.remove(ticker); //TODO: what if few users use one ticker, and one of them decide to delete it? HAHA
    }

    public BinanceFuturesPriceTracker getFuturesByTicker(String ticker) {
        return futuresPriceHolder.get(ticker);
    }
    public BinanceSpotPriceTracker getSpotByTicker(String ticker) {
        return spotPriceHolder.get(ticker);
    }

    public void startProfitTracker(Deal deal,
                                   OrderService orderService,
                                   AlertService alertService,
                                   DealService dealService) {

        if (deal.getTicker() == null) return;
        if (!profitTrackerHolder.containsKey(deal.getId())) {

            ProfitTrackerService profitTrackerService = new ProfitTrackerService(
                    getFuturesByTicker(deal.getTicker()),
                    deal,
                    orderService,
                    alertService,
                    dealService);
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
