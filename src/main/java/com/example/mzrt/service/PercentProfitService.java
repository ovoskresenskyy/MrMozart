package com.example.mzrt.service;

import com.example.mzrt.model.PercentProfit;
import com.example.mzrt.repository.PercentProfitsRepository;
import org.springframework.stereotype.Service;

@Service
public class PercentProfitService {

    private final PercentProfitsRepository percentProfitsRepository;

    public PercentProfitService(PercentProfitsRepository percentProfitsRepository) {
        this.percentProfitsRepository = percentProfitsRepository;
    }

    public PercentProfit findByStrategyIdAndTicker(int strategyId, String ticker) {
        return percentProfitsRepository.findByStrategyIdAndTicker(strategyId, ticker)
                .orElse(PercentProfit.builder()
                        .strategyId(strategyId)
                        .ticker(ticker)
                        .build());
    }

    public void save(PercentProfit profit) {
        percentProfitsRepository.save(profit);
    }
}
