package com.example.mzrt.repository;

import com.example.mzrt.model.Strategy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyRepository extends CrudRepository<Strategy, Integer> {

    List<Strategy> findAll();
}
