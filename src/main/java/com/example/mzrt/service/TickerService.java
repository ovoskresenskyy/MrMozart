package com.example.mzrt.service;

import com.example.mzrt.model.Ticker;
import com.example.mzrt.repository.TickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TickerService {

    private final TickerRepository tickerRepository;

    @Autowired
    public TickerService(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    public List<Ticker> findByUserId(int userId) {
        return tickerRepository.findByUserId(userId, Sort.by(Sort.Direction.ASC, "name"));
    }

    public Ticker findById(int id) {
        return tickerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public Ticker save(Ticker ticker) {
        return tickerRepository.save(ticker);
    }

    public void deleteById(int id) {
        tickerRepository.deleteById(id);
    }
}
