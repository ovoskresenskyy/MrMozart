package com.example.mzrt.repository;

import com.example.mzrt.model.Alert;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends CrudRepository<Alert, Integer> {

    List<Alert> findByUserIdAndStrategyId(int userId, int strategyId, Sort sort);

    Optional<Alert> findByUserIdAndName(int userId, String name);
}
