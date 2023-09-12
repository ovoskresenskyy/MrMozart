package com.example.mzrt.tracker;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;

public class SpotPriceTracker {

    private double price;
    private int streamId;
    private UMWebsocketClientImpl client;

    public SpotPriceTracker() {
        this.price = 0;
    }

    public void startTracking(String ticker) {
        this.client = new UMWebsocketClientImpl("wss://stream.binance.com");
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
