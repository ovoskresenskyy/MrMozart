package com.example.mzrt.service;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;

public class BinancePriceTracker {

    private double price;
    private int streamId;
    private final UMWebsocketClientImpl client;

    public BinancePriceTracker() {
        this.client = new UMWebsocketClientImpl();
    }

    public void startTracking(String ticker) {
        this.streamId = client.aggTradeStream(ticker, ((event) -> {
            this.price = getPriceFromEvent(event);
        }));
    }

    public void closeConnection() {
        client.closeConnection(streamId);
    }

    private double getPriceFromEvent(String event) {
        return Double.parseDouble(
                event.substring(
                                event.indexOf("p\":") + 4,
                                event.indexOf("q\":") - 3)
                        .replaceAll("\"", ""));
    }

    public double getPrice() {
        return price;
    }
}
