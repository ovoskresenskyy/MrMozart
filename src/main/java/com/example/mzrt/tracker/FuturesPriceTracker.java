package com.example.mzrt.tracker;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FuturesPriceTracker {

    private double price;
    private int streamId;
    private UMWebsocketClientImpl client;

    private static final Logger logger = LogManager.getLogger(FuturesPriceTracker.class);

    public FuturesPriceTracker() {
        this.price = 0;
    }

    public void startTracking(String ticker) {
        this.client = new UMWebsocketClientImpl();
        this.streamId = client.aggTradeStream(ticker, ((event) -> {
            this.price = getPriceFromEvent(event);
            logger.info(event);
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
