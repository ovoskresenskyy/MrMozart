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

    /**
     * This method is responsible for handling the received alerts from the TradingView
     * The message comes as text, so we need process the text and make the simple alert from it.
     *
     * @param token   - Users token
     * @param message - Text message inside the alert
     * @param ticker  - Coin pair like BTCUSDT
     * @return Created new order if everything is ok, or empty ony if not.
     */
    public Order handleAlert(String token, String message, String ticker) {
        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        Strategy strategy = strategyService.findById(BF_STRATEGY_ID);
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

        /* If it's wrong message, return empty order. */
        if (alertName.equals("")) {
            return Order.builder().build();
        }

        Alert alert = alertService.findByUserIdAndStrategyIdAndName(userId, BF_STRATEGY_ID, alertName);
        return orderService.sendOpeningOrder(alert,
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
