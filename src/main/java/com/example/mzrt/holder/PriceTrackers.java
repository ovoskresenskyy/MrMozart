package com.example.mzrt.holder;

import com.example.mzrt.tracker.SpotPriceTracker;
import com.example.mzrt.tracker.FuturesPriceTracker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PriceTrackers {

    public final Map<String, FuturesPriceTracker> futuresPriceHolder = new HashMap<>();
    public final Map<String, SpotPriceTracker> spotPriceHolder = new HashMap<>();

    public void startPriceTracking(String ticker) {
        startFuturesTracking(ticker);
        startSpotTracking(ticker);
    }

    private void startFuturesTracking(String ticker){
        if (futuresPriceHolder.containsKey(ticker)) {
            return;
        }
        FuturesPriceTracker futuresPriceTracker = new FuturesPriceTracker();
        futuresPriceTracker.startTracking(ticker);
        futuresPriceHolder.put(ticker, futuresPriceTracker);
    }

    private void startSpotTracking(String ticker){
        if (spotPriceHolder.containsKey(ticker)) return;
        SpotPriceTracker spotPriceTracker = new SpotPriceTracker();
        spotPriceTracker.startTracking(ticker);
        spotPriceHolder.put(ticker, spotPriceTracker);
    }

    public void stopPriceTracking(String ticker) {
        getFuturesTracker(ticker).closeConnection();
        futuresPriceHolder.remove(ticker);

        getSpotByTicker(ticker).closeConnection();
        spotPriceHolder.remove(ticker);
    }

    public FuturesPriceTracker getFuturesTracker(String ticker) {
        return futuresPriceHolder.get(ticker);
    }
    public SpotPriceTracker getSpotByTicker(String ticker) {
        return spotPriceHolder.get(ticker);
    }

}
