package com.example.mzrt.service;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.model.Deal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import static com.example.mzrt.enums.Side.isShort;

@Service
public class DealPriceService implements CryptoConstants {

    private final PercentProfitService percentProfitService;

    public DealPriceService(PercentProfitService percentProfitService) {
        this.percentProfitService = percentProfitService;
    }

    public void setPrices(Deal deal, String alert, double price) {
        setPriceByAlert(deal, alert, price);
        deal.setAveragePrice(getAvgPrice(deal));
        deal.setProfitPrice(getProfitPrice(deal));
    }

    private void setPriceByAlert(Deal deal, String alert, double price) {
        switch (AlertMessage.valueByName(alert).getNumber()) {
            case 1 -> deal.setFirstPrice(price);
            case 2 -> deal.setSecondPrice(price);
            case 3 -> deal.setThirdPrice(price);
            case 4 -> deal.setFourthPrice(price);
            case 5 -> deal.setFifthPrice(price);
        }
    }

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
    private double getProfitPrice(Deal deal) {
        double averagePrice = deal.getAveragePrice();
        double profit = averagePrice * percentProfitService.getPercent(deal) / 100;

        return roundPrice(isShort(deal.getSide())
                ? averagePrice - profit
                : averagePrice + profit);
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
