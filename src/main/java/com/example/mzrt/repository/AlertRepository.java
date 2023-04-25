package com.example.mzrt.repository;

import com.example.mzrt.model.Alert;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends CrudRepository<Alert, Integer> {

    List<Alert> findByUserIdAndStrategyId(int userId, int strategyId, Sort sort);

    Alert findByUserIdAndStrategyIdAndName(int userId, int strategyId, String name);
}
