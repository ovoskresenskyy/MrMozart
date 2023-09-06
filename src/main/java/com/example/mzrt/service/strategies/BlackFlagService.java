package com.example.mzrt.service.strategies;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.mzrt.enums.AlertMessage.*;
import static com.example.mzrt.enums.Side.getStopTrendAlert;

@Service
public class BlackFlagService implements CryptoConstants {

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
     */
    public void handleAlert(String token, String message, String ticker) {
        int userId = userService.findByToken(token).getId();

        /* Check if we need to close the deal immediately. */
        if (isDealClosing(message)) {
            sendDealClosingOrder(message, ticker, userId);
            return;
        }

        Strategy strategy = strategyService.findById(BF_STRATEGY_ID);
        Alert alert = alertService.findByUserIdAndStrategyIdAndName(userId, BF_STRATEGY_ID, message);
        AlertMessage alertMessage = valueByName(alert.getName());

        if (!isAllowToOpenNewDeal(userId, strategy.getName(), ticker, alertMessage)) {
            return;
        }

        Deal deal = dealService.getDealByTicker(userId, strategy, ticker, alert.getSide());
        if (alertMessage.isEntry() && orderService.send(deal, alert)) {
            dealService.updatePricesByAlert(deal, AlertMessage.valueByName(message));
            startProfitTracker(deal);
        }
    }

    private boolean isAllowToOpenNewDeal(int userId, String strategy, String ticker, AlertMessage alertMessage) {
        Optional<Deal> openedDeal = dealService.getOpenedDealByTicker(userId, strategy, ticker);
        return (alertMessage.isEntry() && !alertMessage.isForbiddenToOpenNewDeals())
                || openedDeal.isPresent();
    }

    private void sendDealClosingOrder(String message, String ticker, int userId) {
        Deal deal = getDeal(userId, ticker);
        if (deal == null) {
            return;
        }

        if (isStopTrendText(message)) {
            message = getStopTrendAlert(deal.getSide());
        }

        orderService.send(deal, alertService.getAlert(deal, message));
        dealService.closeDeal(deal, message);
    }

    private void startProfitTracker(Deal deal) {
        BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();
        binanceDataHolder.startProfitTracker(deal);
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
