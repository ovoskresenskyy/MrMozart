package com.example.mzrt.repository;

import com.example.mzrt.model.PercentProfit;
import com.example.mzrt.model.Ticker;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PercentProfitsRepository extends CrudRepository<PercentProfit, Integer> {

    List<PercentProfit> findByTickerId(int tickerId);
}
