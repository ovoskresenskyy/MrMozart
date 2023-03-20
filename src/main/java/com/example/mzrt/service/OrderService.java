package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import com.example.mzrt.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public Order sendOrder(Alert alert, String ticker, int userId, String alertTime) {

        OrderThreadService orderThreadService = new OrderThreadService(alert,
                ticker,
                userId,
                alertTime,
                orderRepository,
                restTemplate);

        Thread t = new Thread(orderThreadService);
        t.start();

        return orderThreadService.getOrder();
    }

    public List<Order> findByUserId(int userId) {
        return orderRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
    }

    public void deleteOrdersByUserId(int userId) {
        orderRepository.deleteOrdersByUserId(userId);
    }
}
