package com.example.mzrt.service.strategies;

import com.example.mzrt.CryptoConstants;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.DealService;
import com.example.mzrt.service.OrderService;
import org.springframework.stereotype.Service;

import static com.example.mzrt.enums.AlertMessage.*;

@Service
public class MozartService implements CryptoConstants {

    private final OrderService orderService;
    private final AlertService alertService;
    private final DealService dealService;

    public MozartService(OrderService orderService,
                         AlertService alertService,
                         DealService dealService) {
        this.orderService = orderService;
        this.alertService = alertService;
        this.dealService = dealService;
    }

    public void handleAlert(String token, String message, String ticker) {
        if (isStopTrendText(message)) {
            sendStopTrend(ticker);
            return;
        }

        Alert alert = alertService.findByStrategyIdAndName(MOZART_STRATEGY_ID, message);
        Deal deal = dealService.findById(MOZART_DEAL_ID);
        orderService.send(deal, alert, ticker);
    }

    private void sendStopTrend(String ticker) {
        Deal deal = dealService.findById(MOZART_DEAL_ID);

        Alert sts = alertService.findByStrategyIdAndName(MOZART_STRATEGY_ID, "STS");
        Alert stl = alertService.findByStrategyIdAndName(MOZART_STRATEGY_ID, "STL");

        orderService.send(deal, sts, ticker);
        orderService.send(deal, stl, ticker);
    }
}
