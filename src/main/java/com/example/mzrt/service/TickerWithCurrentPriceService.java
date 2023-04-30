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

    public List<TickerWithCurrentPrice> findByUserId(int userId) {
        LinkedList<TickerWithCurrentPrice> tickersAndPrices = new LinkedList<>();

        BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();

        for (Ticker ticker : tickerService.findByUserId(userId)) {
            tickersAndPrices.add(
                    TickerWithCurrentPrice.builder()
                            .ticker(ticker)
                            .futuresPrice(binanceDataHolder.getFuturesByTicker(ticker.getName()).getPrice())
                            .spotPrice(binanceDataHolder.getSpotByTicker(ticker.getName()).getPrice())
                            .build());
        }
        return tickersAndPrices;
    }
}
