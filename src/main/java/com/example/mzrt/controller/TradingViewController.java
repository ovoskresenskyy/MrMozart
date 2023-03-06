package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import com.example.mzrt.model.User;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/alert")
public class TradingViewController {

    private final OrderService orderService;
    private final AlertService alertService;
    private final UserService userService;

    @Autowired
    public TradingViewController(OrderService orderService, AlertService alertService, UserService userService) {
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

        Optional<User> user = userService.findByToken(token);
        if (user.isEmpty()) return Order.builder().build();

        int userId = user.get().getId();

        if (alertText.equalsIgnoreCase("Stop Trend")) {
            Optional<Alert> alertShort = alertService.findByUserIdAndName(userId, "STS");
            Optional<Alert> alertLong = alertService.findByUserIdAndName(userId, "STL");

            alertShort.ifPresent(alert -> orderService.sendOrder(alert, ticker, userId));

            return alertLong.isPresent() ? orderService.sendOrder(alertLong.get(), ticker, userId) : Order.builder().build();
        } else {
            Optional<Alert> alert = alertService.findByUserIdAndName(user.get().getId(), alertText);

            return alert.isPresent() ? orderService.sendOrder(alert.get(), ticker, userId) : Order.builder().build();
        }
    }
}
