package com.example.mzrt.repository;

import com.example.mzrt.model.Deal;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealRepository extends CrudRepository<Deal, Integer> {

    Optional<Deal> getByStrategyAndTickerAndOpenTrue(String strategy, String ticker);

    List<Deal> findByOpenTrue();

    List<Deal> getByStrategyIdAndOpenIsTrue(int strategyId, Sort sort);
    List<Deal> getByStrategyIdAndOpenIsFalse(int strategyId, Sort sort);
}
