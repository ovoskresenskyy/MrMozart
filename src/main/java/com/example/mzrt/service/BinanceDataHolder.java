package com.example.mzrt.service;

import java.util.HashMap;
import java.util.Map;

public class BinanceDataHolder {

    private static BinanceDataHolder instance;

    public final Map<String, BinancePriceProvider> holder = new HashMap<>();

    public static synchronized BinanceDataHolder getInstance() {
        if (instance == null) {
            instance = new BinanceDataHolder();
        }
        return instance;
    }

    public BinancePriceProvider getByTicker(String ticker) {
        if (holder.containsKey(ticker)) return holder.get(ticker);

        BinancePriceProvider binancePriceProvider = new BinancePriceProvider();
        binancePriceProvider.startTracking(ticker);
        holder.put(ticker, binancePriceProvider);
        return binancePriceProvider;
    }
}
