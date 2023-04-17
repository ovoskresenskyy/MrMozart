package com.example.mzrt.repository;

import com.example.mzrt.model.Deal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealRepository extends CrudRepository<Deal, Integer> {

    Optional<Deal> getByUserIdAndStrategyAndTickerAndOpenTrue(int userId,
                                                                     String strategy,
                                                                     String ticker);

    List<Deal> getByUserIdAndStrategy(int userId, String strategy);
}
