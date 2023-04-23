package com.example.mzrt.service;

import com.example.mzrt.model.PercentProfit;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.model.TickerWithProfit;
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

    public List<TickerWithProfit> findAllByUserAndStrategy(int userId, int strategyId) {
        LinkedList<TickerWithProfit> tickersAndProfits = new LinkedList<>();

        for (Ticker ticker : tickerService.findByUserId(userId)) {
            tickersAndProfits.add(
                    TickerWithProfit.builder()
                            .ticker(ticker)
                            .percent(getPercentProfit(strategyId, ticker.getName()))
                            .build());
        }
        return tickersAndProfits;
    }

    private PercentProfit getPercentProfit(int strategyId, String ticker){
        return percentProfitService.findByStrategyIdAndTicker(strategyId, ticker);
    }
}
