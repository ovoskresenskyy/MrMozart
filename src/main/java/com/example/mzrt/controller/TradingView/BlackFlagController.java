package com.example.mzrt.controller.TradingView;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
import com.example.mzrt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/bf")
public class BlackFlagController {

    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;
    private final DealService dealService;

    @Autowired
    public BlackFlagController(OrderService orderService,
                               AlertService alertService,
                               UserService userService,
                               DealService dealService) {
        this.orderService = orderService;
        this.alertService = alertService;
        this.userService = userService;
        this.dealService = dealService;
    }

    @PostMapping(value = "/{token}/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public Order getAlert(@PathVariable(value = "ticker") String ticker,
                          @PathVariable(value = "token") String token,
                          @RequestBody String message) {

        ticker = ticker.toUpperCase() + "USDT";
        BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();
        binanceDataHolder.getByTicker(ticker);

        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        int userId = userService.findByToken(token).getId();

        if (message.equalsIgnoreCase("Stop Line Change")) return sendStopTrend(userId,
                ticker,
                alertTime);

        String side = getSide(message);
        if (side.equals("")) return Order.builder().build();

        String alertNumber = getAlertNumber(message);
        if (alertNumber.equals("")) return Order.builder().build();

        return orderService.sendOrder(alertService.findByUserIdAndName(userId, alertNumber + side),
                ticker,
                userId,
                alertTime,
                Strategy.BLACK_FLAG);
    }

    private Order sendStopTrend(int userId, String ticker, String alertTime) {
        orderService.sendOrder(alertService.findByUserIdAndName(userId, "STS"),
                ticker,
                userId,
                alertTime,
                Strategy.BLACK_FLAG);
        orderService.sendOrder(alertService.findByUserIdAndName(userId, "STL"),
                ticker,
                userId,
                alertTime,
                Strategy.BLACK_FLAG);

        DealThreadService dealThreadService = new DealThreadService(dealService, userId, ticker);
        dealThreadService.closeDeal();

        return Order.builder().build();
    }

    private String getSide(String message) {
        if (message.indexOf("long trend") > 0) return "L";
        if (message.indexOf("short trend") > 0) return "S";
        return "";
    }

    private String getAlertNumber(String message) {
        int index = message.indexOf("Fib");
        if (index == 0) return "";

        return String.valueOf(message.charAt(index + 3));
    }

    @PostMapping("/test")
    public Order getTestAlert() {
        return Order.builder()
                .name("test")
                .build();
    }
}
