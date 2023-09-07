package com.example.mzrt.service;

import com.example.mzrt.model.Ticker;
import com.example.mzrt.model.TickerWithCurrentPrice;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class TickerWithCurrentPriceService {

    private final TickerService tickerService;

    public TickerWithCurrentPriceService(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    public List<TickerWithCurrentPrice> findAll() {
        LinkedList<TickerWithCurrentPrice> tickersAndPrices = new LinkedList<>();

        for (Ticker ticker : tickerService.findAll()) {
            tickersAndPrices.add(initNewPriceHolder(ticker));
        }
        return tickersAndPrices;
    }

    private TickerWithCurrentPrice initNewPriceHolder(Ticker ticker) {
        BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();

        String name = ticker.getName();
        double futuresPrice = binanceDataHolder.getFuturesByTicker(name).getPrice();
        double spotPrice = binanceDataHolder.getSpotByTicker(name).getPrice();

        return TickerWithCurrentPrice.builder()
                .ticker(ticker)
                .futuresPrice(futuresPrice)
                .spotPrice(spotPrice)
                .build();

    }
}
