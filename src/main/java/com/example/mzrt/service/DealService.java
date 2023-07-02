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

import static com.example.mzrt.enums.Side.isShort;
import static com.example.mzrt.enums.Side.sideByName;
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

    /**
     * This method creates the new deal and save it in the repository
     * - Strategy name saving in lower case format
     * - Side name wil get from the enum to hold 'short' instead of 'sell' etc
     *
     * @param userId   - ID of user which is that deal
     * @param strategy - Name of the strategy
     * @param ticker   - Name of the pair like 'BTCUSDT'
     * @param side     - Sell / buy (because it's receives from the alert)
     * @return - The created deal, saved into the repository
     */
    public Deal getNewDeal(int userId,
                           Strategy strategy,
                           String ticker,
                           String side) {

        Deal newDeal = Deal.builder()
                .userId(userId)
                .strategy(strategy.getName().toLowerCase())
                .strategyId(strategy.getId())
                .ticker(ticker)
                .side(sideByName(side).name)
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
        setAveragePrice(deal);
        setProfitPrice(deal);
    }

    //TODO: comment
    private int getAlertNumber(String alert) {
        if (alert.equals("1S") || alert.equals("1L")) return 1;
        if (alert.equals("2S") || alert.equals("2L")) return 2;
        if (alert.equals("3S") || alert.equals("3L")) return 3;
        if (alert.equals("4S") || alert.equals("4L")) return 4;
        if (alert.equals("5S") || alert.equals("5L")) return 5;
        return 0;
    }

    //TODO: comment
    private void setAveragePrice(Deal deal) {
        double averagePrice = calculateAveragePrice(deal);
        deal.setAveragePrice(averagePrice);
    }

    //TODO: comment
    private double calculateAveragePrice(Deal deal){
        OptionalDouble averageOptional = DoubleStream.of(
                        deal.getFirstPrice(),
                        deal.getSecondPrice(),
                        deal.getThirdPrice(),
                        deal.getFourthPrice(),
                        deal.getFifthPrice())
                .filter(price -> price != 0)
                .filter(Objects::nonNull)
                .average();

        return averageOptional.isPresent() ? roundPrice(averageOptional.getAsDouble()) : 0;
    }

    //TODO: comment
    private void setProfitPrice(Deal deal) {
        double profitPrice = getProfitPrice(deal);

        deal.setProfitPrice(profitPrice);
        dealRepository.save(deal);
    }

    private double getProfitPrice(Deal deal) {
        double averagePrice = deal.getAveragePrice();
        double takeProfitPercent = getTakeProfitPercent(deal);
        double profit = averagePrice * takeProfitPercent / 100;

        /* For the short positions our profit price always lower than the average price of the deal
         * For the long positions - higher than the average price of the deal */
        boolean aShort = isShort(deal.getSide());
        return roundPrice(aShort ? averagePrice - profit : averagePrice + profit);
    }

    private double getTakeProfitPercent(Deal deal) {
        int strategyId = deal.getStrategyId();
        int tickerId = tickerService.findByNameAndUserId(deal.getTicker(), deal.getUserId()).getId();

        return percentProfitService.findByStrategyIdAndTickerId(strategyId, tickerId).getValue();
    }

    /**
     * This method round the received price to 4 sings
     *
     * @param price - Price to be processed
     * @return Rounded price
     */
    private double roundPrice(double price) {
        BigDecimal bd = new BigDecimal(price)
                .setScale(4, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
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
