package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final StrategyService strategyService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        RestTemplate restTemplate,
                        StrategyService strategyService) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.strategyService = strategyService;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public boolean send(Deal deal, Alert alert) {
        return send(deal, alert, deal.getTicker());
    }

    public boolean send(Deal deal, Alert alert, String ticker) {
        if (OrderPriceService.isRedundant(deal, alert.getName())) {
            return false;
        }

        Order order = createNewOrder(deal,
                strategyService.findById(deal.getStrategyId()),
                alert,
                ticker);
        /* Create pause between receiving the alert and sending order */
        new Thread(new OrderSender(restTemplate, alert, order)).start();
        return true;
    }

    public List<Order> findByDealId(int dealId) {
        return orderRepository.findByDealId(dealId, Sort.by(Sort.Direction.DESC, "id"));
    }

    private Order createNewOrder(Deal deal,
                                 Strategy strategy,
                                 Alert alert,
                                 String ticker) {
        return save(Order.builder()
                .name(alert.getName())
                .strategy(strategy.getName())
                .secret(alert.getSecret())
                .side(alert.getSide())
                .symbol(ticker)
                .userId(deal.getUserId())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .dealId(deal.getId())
                .build());
    }
}
