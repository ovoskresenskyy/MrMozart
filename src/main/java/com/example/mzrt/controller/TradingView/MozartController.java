package com.example.mzrt.controller.TradingView;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.model.Order;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/alert")
public class MozartController {

    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;

    @Autowired
    public MozartController(OrderService orderService, AlertService alertService, UserService userService) {
        this.orderService = orderService;
        this.alertService = alertService;
        this.userService = userService;
    }

    @PostMapping(value = "/{token}/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public Order getAlert(@PathVariable(value = "ticker") String ticker,
                          @PathVariable(value = "token") String token,
                          @RequestBody String alertText) {

        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        int userId = userService.findByToken(token).getId();
        if (alertText.equalsIgnoreCase("Stop Trend")) return sendStopTrend(userId, ticker, alertTime);
        return orderService.sendOrder(alertService.findByUserIdAndName(userId, alertText),
                ticker,
                userId,
                alertTime,
                Strategy.MOZART);
    }

    private Order sendStopTrend(int userId, String ticker, String alertTime) {
        orderService.sendOrder(alertService.findByUserIdAndName(userId, "STS"),
                ticker,
                userId,
                alertTime,
                Strategy.BLACK_FLAG);
        return orderService.sendOrder(alertService.findByUserIdAndName(userId, "STL"),
                ticker,
                userId,
                alertTime,
                Strategy.BLACK_FLAG);
    }

    @PostMapping("/test")
    public Order getTestAlert() {
        return Order.builder()
                .name("test")
                .build();
    }
}
