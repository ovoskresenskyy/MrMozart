package com.example.mzrt.service;

import com.example.mzrt.model.PercentProfit;
import com.example.mzrt.repository.PercentProfitsRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class PercentProfitService {

    private final PercentProfitsRepository percentProfitsRepository;

    public PercentProfitService(PercentProfitsRepository percentProfitsRepository) {
        this.percentProfitsRepository = percentProfitsRepository;
    }

    public PercentProfit findByStrategyIdAndTickerId(int strategyId, int tickerId) {
        return percentProfitsRepository.findByStrategyIdAndTickerId(strategyId, tickerId)
                .orElse(PercentProfit.builder()
                        .strategyId(strategyId)
                        .tickerId(tickerId)
                        .build());
    }

    public void save(PercentProfit profit) {
        percentProfitsRepository.save(profit);
    }
}
