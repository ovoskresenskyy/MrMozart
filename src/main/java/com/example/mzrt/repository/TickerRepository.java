package com.example.mzrt.repository;

import com.example.mzrt.model.Ticker;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TickerRepository extends CrudRepository<Ticker, Integer> {

    List<Ticker> findAll();
    List<Ticker> findByUserId(int userId, Sort sort);

    Ticker findByNameAndUserId(String name, int userId);
}
