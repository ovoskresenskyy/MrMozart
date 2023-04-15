package com.example.mzrt.service;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public static void checkConnection() {

        UMWebsocketClientImpl client = new UMWebsocketClientImpl();
        int streamID1 = client.aggTradeStream("ethusdt",(System.out::println));
    }
}
