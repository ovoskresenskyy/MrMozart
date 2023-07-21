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
import static com.example.mzrt.enums.AlertMessage.isDealClosing;
import static com.example.mzrt.enums.AlertMessage.isTradeEntry;

@Service
public class BlackFlagService {

    private final StrategyService strategyService;
    private final OrderService orderService;
    private final AlertService alertService;
    private final DealService dealService;
    private final DealPriceService dealPriceService;
    private final UserService userService;

    @Autowired
    public BlackFlagService(StrategyService strategyService,
                            OrderService orderService,
                            AlertService alertService,
                            DealService dealService,
                            DealPriceService dealPriceService,
                            UserService userService) {
        this.strategyService = strategyService;
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
        this.dealPriceService = dealPriceService;
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
    public void handleAlert(String token, String message, String ticker) { //TODO decompose
        int userId = userService.findByToken(token).getId();

        /* Check if we need to close the deal immediately. */
        if (isDealClosing(message)) {
            sendDealClosingOrder(message, ticker, userId);
            return;
        }

        Strategy strategy = strategyService.findById(BF_STRATEGY_ID);
        Alert alert = alertService.findByUserIdAndStrategyIdAndName(userId, BF_STRATEGY_ID, message);
        Deal deal = dealService.getDealByTicker(userId, strategy, ticker, alert.getSide());

        boolean orderIsSent = orderService.send(deal, alert);

        if (alert.isOpening() && orderIsSent) {
            BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
            double currentPrice = dataHolder.getFuturesByTicker(deal.getTicker()).getPrice();

            dealPriceService.setPrices(deal, alert.getName(), currentPrice);
            deal.setLastChangeTime(LocalDateTime.now());
            dealService.save(deal);
            startProfitTracker(deal);
        }

        dealService.updateLastChangesTime(deal);

        return order;
    }

    //TODO decompose better
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
        binanceDataHolder.startProfitTracker(deal,
                orderService,
                alertService,
                dealService);
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
