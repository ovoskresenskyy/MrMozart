package com.example.mzrt.service;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import com.example.mzrt.repository.OrderRepository;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Data
public class OrderThreadService implements Runnable {

    private Alert alert;
    private String ticker;
    private int userId;
    private String alertTime;
    private OrderService orderService;
    private RestTemplate restTemplate;
    private Strategy strategy;
    private Order order;

    public OrderThreadService(Alert alert,
                              String ticker,
                              int userId,
                              String alertTime,
                              OrderService orderService,
                              RestTemplate restTemplate,
                              Strategy strategy) {
        this.alert = alert;
        this.ticker = ticker.toUpperCase() + "USDT";
        this.userId = userId;
        this.alertTime = alertTime;
        this.orderService = orderService;
        this.restTemplate = restTemplate;
        this.strategy = strategy;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(alert.getPause() * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Order order = orderService.getOrderByStrategy(strategy,
                alert,
                ticker,
                userId,
                alertTime);

        if (!orderService.orderIsEmpty(order)) postOrder(order);
    }

    private void postOrder(Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        this.order = restTemplate.postForObject(alert.getWebhook(),
                new HttpEntity<>(order, headers),
                Order.class);
    }
}
