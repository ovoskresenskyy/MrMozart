package com.example.mzrt.service;

import com.example.mzrt.model.PercentProfit;
import com.example.mzrt.repository.PercentProfitsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PercentProfitService {

    private final PercentProfitsRepository percentProfitsRepository;

    public PercentProfitService(PercentProfitsRepository percentProfitsRepository) {
        this.percentProfitsRepository = percentProfitsRepository;
    }

    public List<PercentProfit> findByTickerId(int tickerId) {
        return percentProfitsRepository.findByTickerId(tickerId);
    }
}
