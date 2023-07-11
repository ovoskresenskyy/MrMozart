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

    /**
     * This method is responsible for getting the instance of PercentProfit
     * according to the received parameters,
     * or creating the new one if not found
     * <p>
     * It's held separately for each strategy and each ticker
     *
     * @param strategyId - The ID of the strategy
     * @param tickerId   - The ID of the ticker
     * @return - Instance of the PercentProfit
     */
    public PercentProfit getPercentProfit(int strategyId, int tickerId) {
        return percentProfitsRepository.findByStrategyIdAndTickerId(strategyId, tickerId)
                .orElse(PercentProfit.builder()
                        .strategyId(strategyId)
                        .tickerId(tickerId)
                        .build());
    }

    /**
     * This method is responsible for getting the value of exist PercentProfit instance
     * be the received deal. The deal hold the information about the strategy and the ticker.
     *
     * @param deal - The deal as a holder of the strategy and ticker of the percent profit.
     * @return The percent profit of the received ticker for the given strategy
     */
    public double getPercent(Deal deal) {
        int strategyId = deal.getStrategyId();
        int tickerId = tickerService.findByNameAndUserId(deal.getTicker(), deal.getUserId()).getId();

        return getPercentProfit(strategyId, tickerId).getValue();
    }

    public void save(PercentProfit profit) {
        percentProfitsRepository.save(profit);
    }
}
