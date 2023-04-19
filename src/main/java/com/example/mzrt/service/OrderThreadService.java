package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class OrderThreadService implements Runnable {

    private final RestTemplate restTemplate;
    private final Alert alert;
    private final Order order;

    public OrderThreadService(RestTemplate restTemplate,
                              Alert alert,
                              Order order) {
        this.restTemplate = restTemplate;
        this.alert = alert;
        this.order = order;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(alert.getPause() * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForObject(alert.getWebhook(),
                new HttpEntity<>(order, headers),
                Order.class);
    }
}
