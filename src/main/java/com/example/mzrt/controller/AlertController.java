package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AlertController {

    private final OrderService orderService;
    private final AlertService alertService;

    @Autowired
    public AlertController(OrderService orderService, AlertService alertService) {
        this.orderService = orderService;
        this.alertService = alertService;
    }

    @PostMapping(value = "/alert/{ticker}",
            consumes = {"text/plain", "application/*"},
            headers = "content-type=text/json",
            produces = "application/json")
    public Order getAlert(@PathVariable(value = "ticker") String ticker, @RequestBody String alertText) {
        if (alertText.equalsIgnoreCase("Stop Trend")) {
            Alert alertShort = alertService.findByName("STS");
            Alert alertLong = alertService.findByName("STL");

            orderService.sendOrder(alertShort, ticker);

            return orderService.sendOrder(alertLong, ticker);
        } else {
            Alert alert = alertService.findByName(alertText);
            return orderService.sendOrder(alert, ticker);
        }
    }
}
