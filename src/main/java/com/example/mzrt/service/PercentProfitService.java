package com.example.mzrt.service;

import com.example.mzrt.model.Deal;
import com.example.mzrt.model.PercentProfit;
import com.example.mzrt.repository.PercentProfitsRepository;
import org.springframework.stereotype.Service;

@Service
public class PercentProfitService {

    private final PercentProfitsRepository percentProfitsRepository;
    private final TickerService tickerService;

    public PercentProfitService(PercentProfitsRepository percentProfitsRepository,
                                TickerService tickerService) {
        this.percentProfitsRepository = percentProfitsRepository;
        this.tickerService = tickerService;
    }

    public PercentProfit getPercentProfit(int strategyId, int tickerId) {
        return percentProfitsRepository.findByStrategyIdAndTickerId(strategyId, tickerId)
                .orElse(PercentProfit.builder()
                        .strategyId(strategyId)
                        .tickerId(tickerId)
                        .build());
    }

    public double getPercent(Deal deal){
        int strategyId = deal.getStrategyId();
        int tickerId = tickerService.findByNameAndUserId(deal.getTicker(), deal.getUserId()).getId();

        return getPercentProfit(strategyId, tickerId).getValue();
    }

    public void save(PercentProfit profit) {
        percentProfitsRepository.save(profit);
    }
}
