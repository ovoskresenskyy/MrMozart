package com.example.mzrt.service.strategies;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.example.mzrt.CryptoConstants.BF_STRATEGY_ID;
import static com.example.mzrt.enums.AlertMessage.*;
import static com.example.mzrt.enums.Side.*;

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

    /**
     * This method is responsible for handling the received alerts from the TradingView
     * The message comes as text, so we need process the text and make the simple alert from it.
     *
     * @param token   - Users token
     * @param message - Text message inside the alert
     * @param ticker  - Coin pair like BTCUSDT
     * @return Created new order if everything is ok, or empty ony if not.
     */
    public Order handleAlert(String token, String message, String ticker) { //TODO decompose
        int userId = userService.findByToken(token).getId();

        /* Check if we need to close the deal immediately. */
        if (isDealClosing(message)) {
            return sendDealClosingOrder(message, ticker, userId);
        }

        Strategy strategy = strategyService.findById(BF_STRATEGY_ID);
        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        Alert alert = alertService.findByUserIdAndStrategyIdAndName(userId, BF_STRATEGY_ID, message);
        Deal deal = dealService.getDealByTicker(userId, strategy, ticker, alert.getSide());
        Order order = orderService.placeOrder(deal, alert, alertTime);

        if (alert.isOpening()) {
            dealService.setPrices(deal, alert.getName(), order.getPrice());
            startProfitTracker(deal);
        }

        dealService.updateLastChangesTime(deal);

        return order;
    }

    private Order sendDealClosingOrder(String message, String ticker, int userId) {
        Deal deal = getDeal(userId, ticker);

        if (deal == null) {
            return Order.builder().build();
        }

        Order order = sendClosingOrder(deal, message);

        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
        double currentPrice = dataHolder.getFuturesByTicker(ticker).getPrice();

        dealService.closeDeal(deal, currentPrice, message);

        dataHolder.stopProfitTracker(deal.getId());

        return order;
    }

    private void startProfitTracker(Deal deal) {
        BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();
        binanceDataHolder.startProfitTracker(deal,
                orderService,
                alertService,
                dealService);
    }

    private Order sendClosingOrder(Deal deal, String message) {
        Alert alert = getAlert(deal, message);
        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return orderService.placeOrder(deal, alert, alertTime);
    }

    private Alert getAlert(Deal deal, String message) {
        if (isStopTrendText(message)) {
            message = getStopTrendAlert(deal.getSide());
        }
        return alertService.findByUserIdAndStrategyIdAndName(deal.getUserId(), BF_STRATEGY_ID, message);
    }

    private Deal getDeal(int userId, String ticker) {
        Strategy strategy = strategyService.findById(BF_STRATEGY_ID);
        Optional<Deal> openedDeal = dealService.getOpenedDealByTicker(userId, strategy.getName(), ticker);

        if (openedDeal.isEmpty()) {
            return null;
        }
        return openedDeal.get();
    }
}
