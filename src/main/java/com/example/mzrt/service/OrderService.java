package com.example.mzrt.service;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Order;
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

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final DealService dealService;

    @Autowired
    public OrderService(RestTemplate restTemplate, OrderRepository orderRepository, DealService dealService) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.dealService = dealService;
    }

    public Order sendOrder(Alert alert, String ticker, int userId, String alertTime, Strategy strategy) {

        OrderThreadService orderThreadService = new OrderThreadService(alert,
                ticker,
                userId,
                alertTime,
                this,
                restTemplate,
                strategy);

        Thread t = new Thread(orderThreadService);
        t.start();

        return orderThreadService.getOrder();
    }

    public List<Order> findByUserId(int userId) {
        return orderRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Order> findByDealId(int dealId) {
        return orderRepository.findByDealId(dealId, Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Order> findByUserIdAndStrategy(int userId, String strategy) {
        return orderRepository.findByUserIdAndStrategy(
                userId,
                strategy,
                Sort.by(Sort.Direction.DESC, "id"));
    }

    public void deleteOrdersByUserId(int userId) {
        orderRepository.deleteOrdersByUserId(userId);
    }

    public boolean orderIsEmpty(Order order) {
        return order.getName().equals("") || order.getUserId() == 0;
    }

    public Order getOrderByStrategy(Strategy strategy,
                                    Alert alert,
                                    String ticker,
                                    int userId,
                                    String alertTime) {

        return switch (strategy) {
            case BLACK_FLAG -> getOrderWithDeal(
                    strategy,
                    alert,
                    ticker,
                    userId,
                    alertTime);
            case MOZART -> getOrderWithoutDeal(
                    strategy,
                    alert,
                    ticker,
                    userId,
                    alertTime,
                    2,
                    0);
        };
    }

    private Order getOrderWithDeal(Strategy strategy,
                                   Alert alert,
                                   String ticker,
                                   int userId,
                                   String alertTime) {

        Deal deal = dealService.getOpenedDealByTicker(userId, strategy.name.toLowerCase(), ticker);

        double price = 0;
        if (alert.getNumber() != 0) { //TODO: Need to remake this shit
            if (dealService.orderIsPresent(deal, alert.getNumber())) return Order.builder().build();

            BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();
            price = binanceDataHolder.getByTicker(ticker).getPrice();
        }

        Order order = getOrderWithoutDeal(
                strategy,
                alert,
                ticker,
                userId,
                alertTime,
                deal.getId(),
                price);

        if (alert.getNumber() != 0) dealService.setPrice(deal, alert.getNumber(), price); //TODO: Need to remake this shit

        return order;
    }

    private Order getOrderWithoutDeal(Strategy strategy,
                                      Alert alert,
                                      String ticker,
                                      int userId,
                                      String alertTime,
                                      int dealId,
                                      double price) {

        return orderRepository.save(
                Order.builder()
                        .name(alert.getName())
                        .strategy(strategy.name.toLowerCase())
                        .secret(alert.getSecret())
                        .side(alert.getSide())
                        .symbol(ticker)
                        .userId(userId)
                        .timestamp(alertTime)
                        .dealId(dealId)
                        .price(price)
                        .timestampSent(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                        .build());
    }


}
