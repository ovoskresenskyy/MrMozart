package com.example.mzrt.repository;

import com.example.mzrt.model.StrategyTicker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StrategyTickerRepository extends CrudRepository<StrategyTicker, Integer> {

    Optional<StrategyTicker> findByTickerId(int tickerId);
}
