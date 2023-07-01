package com.example.mzrt.service;

import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class DealService {

    private final DealRepository dealRepository;
    private final PercentProfitService percentProfitService;
    private final TickerService tickerService;

    @Autowired
    public DealService(DealRepository dealRepository, PercentProfitService percentProfitService, TickerService tickerService) {
        this.dealRepository = dealRepository;
        this.percentProfitService = percentProfitService;
        this.tickerService = tickerService;
    }


    public Deal findById(int id) {
        return dealRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public List<Deal> findAllOpened() {
        return dealRepository.findByOpenTrue();
    }

    public Deal save(Deal deal) {
        return dealRepository.save(deal);
    }

    public Optional<Deal> getOpenedDealByTicker(int userId,
                                                String strategy,
                                                String ticker) {
        return dealRepository.getByUserIdAndStrategyAndTickerAndOpenTrue(userId,
                strategy.toLowerCase(),
                ticker);
    }

    public Deal getNewDeal(int userId,
                           Strategy strategy,
                           String ticker,
                           String side) {

        String strategyName = strategy.getName().toLowerCase();
        String sideName = Side.valueByAction(side).name;

        Deal newDeal = Deal.builder()
                .userId(userId)
                .strategy(strategyName)
                .strategyId(strategy.getId())
                .ticker(ticker)
                .side(sideName)
                .open(true)
                .build();

        return dealRepository.save(newDeal);
    }

    public List<Deal> getByUserIdAndStrategyId(int userId, int strategyId, boolean isOpen) {
        return dealRepository.getByUserIdAndStrategyIdAndOpen(userId,
                strategyId,
                isOpen,
                Sort.by(Sort.Direction.DESC, "id"));
    }

    public void setPrice(Deal deal, String alert, double price) {
        switch (getAlertNumber(alert)) {
            case 1 -> deal.setFirstPrice(price);
            case 2 -> deal.setSecondPrice(price);
            case 3 -> deal.setThirdPrice(price);
            case 4 -> deal.setFourthPrice(price);
            case 5 -> deal.setFifthPrice(price);
        }
        calculateAveragePrice(deal);
    }

    private int getAlertNumber(String alert) {
        if (alert.equals("1S") || alert.equals("1L")) return 1;
        if (alert.equals("2S") || alert.equals("2L")) return 2;
        if (alert.equals("3S") || alert.equals("3L")) return 3;
        if (alert.equals("4S") || alert.equals("4L")) return 4;
        if (alert.equals("5S") || alert.equals("5L")) return 5;
        return 0;
    }

    private void calculateAveragePrice(Deal deal) {

        OptionalDouble average = DoubleStream.of(
                        deal.getFirstPrice(),
                        deal.getSecondPrice(),
                        deal.getThirdPrice(),
                        deal.getFourthPrice(),
                        deal.getFifthPrice())
                .filter(price -> price != 0)
                .filter(Objects::nonNull)
                .average();

        deal.setAveragePrice(average.isPresent()
                ? BigDecimal.valueOf(average.getAsDouble())
                .setScale(4, RoundingMode.FLOOR)
                .doubleValue()
                : 0);

        calculateProfitPrice(deal);
    }

    private void calculateProfitPrice(Deal deal) {

        double avgPrice = deal.getAveragePrice();
        double takeProfitPercent = percentProfitService.findByStrategyIdAndTickerId(
                        deal.getStrategyId(),
                        tickerService.findByNameAndUserId(
                                        deal.getTicker(),
                                        deal.getUserId())
                                .getId())
                .getValue();
        double profit = BigDecimal.valueOf(avgPrice * takeProfitPercent / 100)
                .setScale(4, RoundingMode.FLOOR)
                .doubleValue();

        deal.setProfitPrice(deal.getSide().equals("buy") ? avgPrice + profit : avgPrice - profit);
        dealRepository.save(deal);
    }

    public boolean orderIsPresent(Deal deal, String alert) {
        return switch (getAlertNumber(alert)) {
            case 1 -> deal.getFirstPrice() > 0;
            case 2 -> deal.getSecondPrice() > 0;
            case 3 -> deal.getThirdPrice() > 0;
            case 4 -> deal.getFourthPrice() > 0;
            case 5 -> deal.getFifthPrice() > 0;
            default -> true;
        };
    }

    public boolean bestOrderIsPresent(Deal deal, String alert) {
        return switch (getAlertNumber(alert)) {
            case 1 -> (deal.getSecondPrice() + deal.getThirdPrice() + deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 2 -> (deal.getThirdPrice() + deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 3 -> (deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 4 -> deal.getFifthPrice() > 0;
            default -> false;
        };
    }
}
