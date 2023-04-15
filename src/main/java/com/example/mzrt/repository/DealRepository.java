package com.example.mzrt.repository;

import com.example.mzrt.model.Deal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealRepository extends CrudRepository<Deal, Integer> {

    Optional<Deal> getByUserIdAndTickerAndSideAndOpenTrue(int userId, String ticker, String side);
}
