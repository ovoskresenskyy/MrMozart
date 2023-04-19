package com.example.mzrt.repository;

import com.example.mzrt.model.Ticker;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TickerRepository extends CrudRepository<Ticker, Integer> {

    List<Ticker> findByUserId(int userId, Sort sort);

    Optional<Ticker> findByUserIdAndName(int userId, String name);
}
