package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import com.example.mzrt.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(RestTemplate restTemplate, OrderRepository orderRepository) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
    }

    public Order sendOrder(Alert alert, String ticker) {

        Order order = orderRepository.save(Order.builder()
                .name(alert.getName() + " " + ticker)
                .secret(alert.getSecret())
                .side(alert.getSide())
                .symbol(ticker.toUpperCase() + "USDT")
                .build());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> entity = new HttpEntity<>(order, headers);

        System.out.println(order);
        return restTemplate.postForObject(alert.getWebhook(),
                entity,
                Order.class);
    }

    public List<Order> findAll() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
