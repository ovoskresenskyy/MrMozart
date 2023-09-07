package com.example.mzrt.service;

import com.example.mzrt.model.StrategyTicker;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.repository.StrategyTickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class StrategyTickerService {

    private final StrategyTickerRepository strategyTickerRepository;
    private final TickerService tickerService;

    @Autowired
    public StrategyTickerService(StrategyTickerRepository strategyTickerRepository,
                                 TickerService tickerService) {
        this.strategyTickerRepository = strategyTickerRepository;
        this.tickerService = tickerService;
    }

    public StrategyTicker findById(int id) {
        return strategyTickerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    /**
     * Find all tickers by strategy.
     * If it's not present - create new
     *
     * @param strategyId - Is of the strategy
     * @return The list of tickers matched with the strategy
     */
    public List<StrategyTicker> findAllByStrategy(int strategyId) {
        LinkedList<StrategyTicker> strategyTickers = new LinkedList<>();

        for (Ticker ticker : tickerService.findAll()) {
            strategyTickers.add(getOrCreateNew(strategyId, ticker));
        }
        return strategyTickers;
    }

    /**
     * This method is trying to get previously added entity from the
     * repository, or create the new one if it's not present.
     *
     * @param strategyId - The ID of the strategy
     * @param ticker     - The ticker on which are strategy ticker based on
     * @return Strategy ticker
     */
    private StrategyTicker getOrCreateNew(int strategyId, Ticker ticker) {
        Optional<StrategyTicker> maybeStrategyTicker = strategyTickerRepository.findByTickerId(ticker.getId());
        return maybeStrategyTicker.orElseGet(() -> strategyTickerRepository.save(add(strategyId, ticker)));
    }

    private StrategyTicker add(int strategyId, Ticker ticker) {
        return StrategyTicker.builder()
                .tickerId(ticker.getId())
                .name(ticker.getName())
                .strategyId(strategyId)
                .percentTP1(0)
                .percentTP2(0)
                .percentTP3(0)
                .percentTP4(0)
                .percentTP5(0)
                .stopWhenUsed(false)
                .build();
    }

    public StrategyTicker save(StrategyTicker ticker) {
        return strategyTickerRepository.save(ticker);
    }
}
