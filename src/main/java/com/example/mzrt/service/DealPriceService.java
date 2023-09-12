package com.example.mzrt.service;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.holder.PriceTrackers;
import com.example.mzrt.model.Deal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import static com.example.mzrt.enums.Side.isShort;

/**
 * This class is responsible for update entry, take profit, average and profit prices
 */
@Service
public class DealPriceService implements CryptoConstants {

    private final StrategyTickerService strategyTickerService;
    private final PriceTrackers priceTrackers;

    public DealPriceService(StrategyTickerService strategyTickerService,
                            PriceTrackers priceTrackers) {
        this.strategyTickerService = strategyTickerService;
        this.priceTrackers = priceTrackers;
    }

    /**
     * This method is responsible for update all prices according to the
     * received alert inside the given deal
     *
     * @param deal  - The deal to bo updated
     * @param alert - Received alert
     */
    public void updatePrices(Deal deal, AlertMessage alert) {
        if (alert.isEntry()) {
            setEntryPrice(deal, alert);
            deal.setAveragePrice(getAvgPrice(deal));
            deal.setProfitPrice(getProfitPrice(deal));
        } else {
            setTakeProfitPrice(deal, alert);
            double profitPrice = getProfitPrice(deal);
            deal.setProfitPrice(profitPrice);
        }
    }

    /**
     * This method is responsible for setting the entry prices according
     * to the received alert
     * <p>
     * Example: 1s -> setFirstPrice
     *
     * @param deal  - The deal to be updated
     * @param alert - Received alert
     */
    private void setEntryPrice(Deal deal, AlertMessage alert) {
        double currentPrice = priceTrackers.getFuturesTracker(deal.getTicker()).getPrice();

        switch (alert) {
            case S1, L1 -> deal.setFirstPrice(currentPrice);
            case S2, L2 -> deal.setSecondPrice(currentPrice);
            case S3, L3 -> deal.setThirdPrice(currentPrice);
            case S4, L4 -> deal.setFourthPrice(currentPrice);
            case S5, L5 -> deal.setFifthPrice(currentPrice);
        }
    }

    /**
     * This method is responsible for setting the take profit prices according
     * to the received alert
     * <p>
     * Example: STP1 -> setTakePrice1
     *
     * @param deal  - The deal to be updated
     * @param alert - Received alert
     */
    private void setTakeProfitPrice(Deal deal, AlertMessage alert) {
        double currentPrice = priceTrackers.getFuturesTracker(deal.getTicker()).getPrice();

        switch (alert) {
            case STP1, LTP1 -> deal.setTakePrice1(currentPrice);
            case STP2, LTP2 -> deal.setTakePrice2(currentPrice);
            case STP3, LTP3 -> deal.setTakePrice3(currentPrice);
            case STP4, LTP4 -> deal.setTakePrice4(currentPrice);
            case STP5, LTP5 -> deal.setTakePrice5(currentPrice);
        }
    }

    /**
     * This method is calculates the average price bz getting all not empty entry prices
     *
     * @param deal - The deal within which will find the prices
     * @return - Average price
     */
    private double getAvgPrice(Deal deal) {
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

    /**
     * This method is responsible for calculating the profit price
     * by getting average price of the deal and adding or subtracting profit
     * according to the Side
     * <p>
     * For the short positions our profit price always lower than the average price of the deal
     * For the long positions - higher than the average price of the deal
     *
     * @param deal - The deal to be processed
     * @return - Calculated profit price
     */
    public double getProfitPrice(Deal deal) {
        double averagePrice = deal.getAveragePrice();
        double percent = strategyTickerService.getPercent(deal, getTPNumber(deal));
        double profit = averagePrice * percent / 100;

        return roundPrice(isShort(deal.getSide())
                ? averagePrice - profit
                : averagePrice + profit);
    }

    private int getTPNumber(Deal deal) {
        if (deal.getTakePrice1() == 0) {
            return 1;
        } else if (deal.getTakePrice2() == 0) {
            return 2;
        } else if (deal.getTakePrice3() == 0) {
            return 3;
        } else if (deal.getTakePrice4() == 0) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * This method round the received price to 4 sings
     *
     * @param price - Price to be processed
     * @return Rounded price
     */
    private double roundPrice(double price) {
        BigDecimal bd = new BigDecimal(price).setScale(ROUNDING_ACCURACY, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

}
