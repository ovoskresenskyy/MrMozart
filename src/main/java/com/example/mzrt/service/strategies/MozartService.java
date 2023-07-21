package com.example.mzrt.service.strategies;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.DealService;
import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.UserService;
import org.springframework.stereotype.Service;

import static com.example.mzrt.CryptoConstants.MOZART_DEAL_ID;
import static com.example.mzrt.CryptoConstants.MOZART_STRATEGY_ID;
import static com.example.mzrt.enums.AlertMessage.isStopTrend;

@Service
public class MozartService {

    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;
    private final DealService dealService;

    public MozartService(OrderService orderService,
                         AlertService alertService,
                         UserService userService,
                         DealService dealService) {
        this.orderService = orderService;
        this.alertService = alertService;
        this.userService = userService;
        this.dealService = dealService;
    }

    public void handleAlert(String token, String message, String ticker) {
        int userId = userService.findByToken(token).getId();

        if (isStopTrend(message)) {
            sendStopTrend(userId, ticker);
            return;
        }

        Alert alert = alertService.findByUserIdAndStrategyIdAndName(userId, MOZART_STRATEGY_ID, message);
        Deal deal = dealService.findById(MOZART_DEAL_ID);
        orderService.send(deal, alert, ticker);
    }

    private void sendStopTrend(int userId, String ticker) {
        Deal deal = dealService.findById(MOZART_DEAL_ID);

        Alert sts = alertService.findByUserIdAndStrategyIdAndName(userId, MOZART_STRATEGY_ID, "STS");
        Alert stl = alertService.findByUserIdAndStrategyIdAndName(userId, MOZART_STRATEGY_ID, "STL");

        orderService.send(deal, sts, ticker);
        orderService.send(deal, stl, ticker);
    }
}
