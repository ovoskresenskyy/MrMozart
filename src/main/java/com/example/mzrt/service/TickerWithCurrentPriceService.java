package com.example.mzrt.service;

import com.example.mzrt.holder.PriceTrackers;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.model.TickerWithCurrentPrice;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class TickerWithCurrentPriceService {

    private final TickerService tickerService;
    private final PriceTrackers priceTrackers;

    public TickerWithCurrentPriceService(TickerService tickerService,
                                         PriceTrackers priceTrackers) {
        this.tickerService = tickerService;
        this.priceTrackers = priceTrackers;
    }

    public List<TickerWithCurrentPrice> findAll() {
        LinkedList<TickerWithCurrentPrice> tickersAndPrices = new LinkedList<>();

        for (Ticker ticker : tickerService.findAll()) {
            tickersAndPrices.add(initNewPriceHolder(ticker));
        }
        return tickersAndPrices;
    }

    private TickerWithCurrentPrice initNewPriceHolder(Ticker ticker) {
        String name = ticker.getName();
        double futuresPrice = priceTrackers.getFuturesTracker(name).getPrice();
        double spotPrice = priceTrackers.getSpotByTicker(name).getPrice();

        return TickerWithCurrentPrice.builder()
                .ticker(ticker)
                .futuresPrice(futuresPrice)
                .spotPrice(spotPrice)
                .build();

    }
}
