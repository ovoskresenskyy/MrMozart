package com.example.mzrt.service;

import com.example.mzrt.model.Ticker;
import com.example.mzrt.model.TickerWithProfits;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class TickerWithProfitService {

    private final TickerService tickerService;
    private final PercentProfitService percentProfitService;

    public TickerWithProfitService(TickerService tickerService, PercentProfitService percentProfitService) {
        this.tickerService = tickerService;
        this.percentProfitService = percentProfitService;
    }

    public List<TickerWithProfits> findAllByUserId(int userId) {
        LinkedList<TickerWithProfits> tickerWithProfitServices = new LinkedList<>();
        List<Ticker> tickers = tickerService.findByUserId(userId);

        for (Ticker ticker : tickers) {
            tickerWithProfitServices.add(TickerWithProfits.builder()
                    .ticker(ticker)
                    .percents(percentProfitService.findByTickerId(ticker.getId()))
                    .build());
        }
        return tickerWithProfitServices;
    }
}
