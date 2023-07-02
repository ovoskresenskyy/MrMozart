package com.example.mzrt.service.strategies;

import com.example.mzrt.enums.Side;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.mzrt.CryptoConstants.BF_STRATEGY_ID;
import static com.example.mzrt.enums.AlertMessage.isStopLoss;
import static com.example.mzrt.enums.Side.getClosingAlert;

@Service
public class BlackFlagService {

    private final StrategyService strategyService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final DealService dealService;
    private final UserService userService;

    @Autowired
    public BlackFlagService(StrategyService strategyService,
                            OrderService orderService,
                            AlertService alertService,
                            DealService dealService,
                            UserService userService) {
        this.strategyService = strategyService;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
        this.userService = userService;
    }

    public Order handleAlert(String token, String message, String ticker, String alertTime) {

        int userId = userService.findByToken(token).getId();

        /* Check if it StopLoss, so send it immediately. */
        if (isStopLoss(message)) {
            return sendStopLoss(userId,
                    ticker,
                    alertTime,
                    strategy);
        }

        /* For BlackFlag strategy need to separate alert from the text message. */
        String alertName = alertService.getAlertFromMessage(message);

        String alertNumber = getAlertNumber(message);
        if (alertNumber.equals("")) return Order.builder().build();

        return orderService.sendOpeningOrder(alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        2,
                        alertNumber + side),
                ticker,
                userId,
                alertTime,
                strategy);

    }

    private Order sendStopLoss(int userId, String ticker, String alertTime, Strategy strategy) {
        Optional<Deal> openedDealByTicker = dealService.getOpenedDealByTicker(
                userId,
                strategy.getName(),
                ticker);

        if (openedDealByTicker.isEmpty()) return Order.builder().build();

        Deal deal = openedDealByTicker.get();
        String closingAlert = getClosingAlert(deal.getSide());

        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
        double currentPrice = dataHolder.getFuturesByTicker(ticker).getPrice();

        Order order = orderService.sendClosingOrder(alertService.findByUserIdAndStrategyIdAndName(
                        userId,
                        2,
                        closingAlert),
                ticker,
                userId,
                alertTime,
                strategy,
                deal.getId(),
                currentPrice);

        deal.setOpen(false);
        deal.setClosingPrice(currentPrice);
        deal.setClosingAlert(closingAlert);
        dealService.save(deal);

        dataHolder.stopProfitTracker(deal.getId());

        return order;
    }

}
