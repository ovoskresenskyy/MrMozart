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
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DealService dealService;
    private final AlertService alertService;
    private final RestTemplate restTemplate;
    private final StrategyService strategyService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        DealService dealService,
                        AlertService alertService,
                        RestTemplate restTemplate,
                        StrategyService strategyService) {
        this.orderRepository = orderRepository;
        this.dealService = dealService;
        this.alertService = alertService;
        this.restTemplate = restTemplate;
        this.strategyService = strategyService;
    }

    public Order sendOpeningOrder(Alert alert, String ticker, int userId, String alertTime, Strategy strategy) {

        Order order = getOrderByStrategy(strategy,
                alert,
                ticker,
                userId,
                alertTime);

        if (orderIsEmpty(order)) return order;

        Thread t = new Thread(new OrderThreadService(restTemplate,
                alert,
                order));
        t.start();

        order.setTimestampSent(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        return save(order);
    }

    public Order sendClosingOrder(Alert alert,
                                  String ticker,
                                  int userId,
                                  String alertTime,
                                  Strategy strategy,
                                  int dealId,
                                  double price) {

        Order order = createNewOrder(strategy,
                alert,
                ticker,
                userId,
                alertTime,
                dealId,
                price);

        Thread t = new Thread(new OrderThreadService(restTemplate, alert, order));
        t.start();

        order.setTimestampSent(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        return save(order);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> findByUserId(int userId) {
        return orderRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Order> findByDealId(int dealId) {
        return orderRepository.findByDealId(dealId, Sort.by(Sort.Direction.DESC, "id"));
    }

    public boolean orderIsEmpty(Order order) {
        return order.getName() == null || order.getUserId() == 0;
    }

    public Order getOrderByStrategy(Strategy strategy,
                                    Alert alert,
                                    String ticker,
                                    int userId,
                                    String alertTime) {

        return strategy.isUsesDeal()
                ?
                getOrderWithDeal(
                        strategy,
                        alert,
                        ticker,
                        userId,
                        alertTime)
                :
                createNewOrder(
                        strategy,
                        alert,
                        ticker,
                        userId,
                        alertTime,
                        2,
                        0);
    }

    private Order getOrderWithDeal(Strategy strategy,
                                   Alert alert,
                                   String ticker,
                                   int userId,
                                   String alertTime) {

        Optional<Deal> openedDealByTicker = dealService.getOpenedDealByTicker(
                userId,
                strategy.getName(),
                ticker);

        Deal deal = openedDealByTicker.orElseGet(() -> dealService.getNewDeal(
                userId,
                strategy,
                ticker,
                alert.getSide()));

        BinanceDataHolder binanceDataHolder = BinanceDataHolder.getInstance();

        double price = 0;
        if (alert.isOpening()) {
            if (dealService.orderIsPresent(deal, alert.getName())
                    || dealService.bestOrderIsPresent(deal, alert.getName())) return Order.builder().build();
            price = binanceDataHolder.getFuturesByTicker(ticker).getPrice();
        }

        Order order = createNewOrder(
                strategy,
                alert,
                ticker,
                userId,
                alertTime,
                deal.getId(),
                price);

        if (alert.isOpening()) {
            dealService.setPrice(deal, alert.getName(), price);
            binanceDataHolder.startProfitTracker(deal,
                    this,
                    alertService,
                    dealService,
                    strategyService);
        }
        return order;
    }

    private Order createNewOrder(Strategy strategy,
                                 Alert alert,
                                 String ticker,
                                 int userId,
                                 String alertTime,
                                 int dealId,
                                 double price) {

        return Order.builder()
                .name(alert.getName())
                .strategy(strategy.getName())
                .secret(alert.getSecret())
                .side(alert.getSide())
                .symbol(ticker)
                .userId(userId)
                .timestamp(alertTime)
                .dealId(dealId)
                .price(price)
                .build();
    }
}
