package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import com.example.mzrt.repository.OrderRepository;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class OrderThreadService implements Runnable {

    private Alert alert;
    private String ticker;
    private int userId;
    private String alertTime;
    private OrderRepository orderRepository;
    private RestTemplate restTemplate;
    private Order order;

    public OrderThreadService(Alert alert,
                              String ticker,
                              int userId,
                              String alertTime,
                              OrderRepository orderRepository,
                              RestTemplate restTemplate) {
        this.alert = alert;
        this.ticker = ticker;
        this.userId = userId;
        this.alertTime = alertTime;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;

    }

    @Override
    public void run() {
        try {
            Thread.sleep(alert.getPause() * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Order order = orderRepository.save(Order.builder()
                .name(alert.getName())
                .secret(alert.getSecret())
                .side(alert.getSide())
                .symbol(ticker.toUpperCase() + "USDT")
                .userId(userId)
                .timestamp(alertTime)
                .timestampSent(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .build());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> entity = new HttpEntity<>(order, headers);

        System.out.println(order);
        this.order = restTemplate.postForObject(alert.getWebhook(),
                entity,
                Order.class);

    }
}
