package com.example.mzrt.repository;

import com.example.mzrt.model.PercentProfit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PercentProfitsRepository extends CrudRepository<PercentProfit, Integer> {

    Optional<PercentProfit> findByStrategyIdAndTicker(int strategyId, String ticker);
}
