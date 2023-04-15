package com.example.mzrt.service;

import com.example.mzrt.model.Deal;
import com.example.mzrt.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class DealService {

    private final DealRepository dealRepository;

    @Autowired
    public DealService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }


    public Deal findById(int id) {
        return dealRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public Deal getOpenedDealByTicker(int userId,
                                      String strategy,
                                      String ticker,
                                      String side) {

        Optional<Deal> existedDeal = dealRepository.getByUserIdAndStrategyAndTickerAndSideAndOpenTrue(userId,
                strategy,
                ticker,
                side);
        return existedDeal.orElseGet(() -> dealRepository.save(Deal.builder()
                .userId(userId)
                .strategy(strategy)
                .ticker(ticker)
                .side(side)
                .build()));
    }

    public List<Deal> getByUserIdAndStrategy(int userId, String strategy) {
        return dealRepository.getByUserIdAndStrategy(userId, strategy);
    }

    public void setPrice(Deal deal, int alertNumber, int price) {
        switch (alertNumber) {
            case 1 -> deal.setFirstPrice(price);
            case 2 -> deal.setSecondPrice(price);
            case 3 -> deal.setThirdPrice(price);
            case 4 -> deal.setFourthPrice(price);
            case 5 -> deal.setFifthPrice(price);
        }
        calculateAveragePrice(deal);
    }

    private void calculateAveragePrice(Deal deal) {
        OptionalDouble average = DoubleStream.of(deal.getFifthPrice(),
                        deal.getSecondPrice(),
                        deal.getThirdPrice(),
                        deal.getFourthPrice(),
                        deal.getFifthPrice())
                .filter(price -> price != 0)
                .average();

        deal.setAveragePrice(average.isPresent() ? average.getAsDouble() : 0);
        dealRepository.save(deal);
    }

    public boolean orderIsPresent(Deal deal, int alertNumber) {
        return switch (alertNumber) {
            case 1 -> deal.getFirstPrice() > 0;
            case 2 -> deal.getSecondPrice() > 0;
            case 3 -> deal.getThirdPrice() > 0;
            case 4 -> deal.getFourthPrice() > 0;
            case 5 -> deal.getFifthPrice() > 0;
            default -> true;
        };
    }
}
