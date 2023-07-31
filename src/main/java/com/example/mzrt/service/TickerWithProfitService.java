package com.example.mzrt.service;

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
                            .percent1(percentProfitService.getPercentProfit(strategyId,
                                    ticker.getId(),
                                    1))
                            .percent2(percentProfitService.getPercentProfit(strategyId,
                                    ticker.getId(),
                                    2))
                            .percent3(percentProfitService.getPercentProfit(strategyId,
                                    ticker.getId(),
                                    3))
                            .percent4(percentProfitService.getPercentProfit(strategyId,
                                    ticker.getId(),
                                    4))
                            .percent5(percentProfitService.getPercentProfit(strategyId,
                                    ticker.getId(),
                                    5))
                            .build());
        }
        return tickersAndProfits;
    }
}
