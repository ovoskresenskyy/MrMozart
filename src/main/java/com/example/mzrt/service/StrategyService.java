package com.example.mzrt.service;

import com.example.mzrt.model.Strategy;
import com.example.mzrt.model.Ticker;
import com.example.mzrt.repository.StrategyRepository;
import com.example.mzrt.repository.TickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;

    @Autowired
    public StrategyService(StrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    public List<Strategy> findAll(){
        return strategyRepository.findAll();
    }

}
