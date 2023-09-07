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

        BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();

        for (Ticker ticker : tickerService.findAll()) {
            String name = ticker.getName();
            double futuresPrice = binanceDataHolder.getFuturesByTicker(name).getPrice();
            double spotPrice = binanceDataHolder.getSpotByTicker(name).getPrice();

            TickerWithCurrentPrice tickerWithPrice = TickerWithCurrentPrice.builder()
                    .ticker(ticker)
                    .futuresPrice(futuresPrice)
                    .spotPrice(spotPrice)
                    .build();

            tickersAndPrices.add(tickerWithPrice);
        }
        return tickersAndPrices;
    }
}
