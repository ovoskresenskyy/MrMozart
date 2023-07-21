package com.example.mzrt.service;

import com.example.mzrt.enums.AlertMessage;
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

import static com.example.mzrt.enums.AlertMessage.isTradeEntry;

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
        if (alert.isOpening() && isRedundant(deal, alert)) {
            return false;
        }

        Order order = createNewOrder(deal,
                strategyService.findById(deal.getStrategyId()),
                alert,
                ticker);
        /* Create pause between receiving the alert and sending order */
        new Thread(new OrderThreadService(restTemplate, alert, order)).start();
        order.setTimestampSent(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        return save(order);
    }

    public List<Order> findByDealId(int dealId) {
        return orderRepository.findByDealId(dealId, Sort.by(Sort.Direction.DESC, "id"));
    }

    private Order getOrder(Deal deal, Alert alert, String alertTime, String ticker) {
        Strategy strategy = strategyService.findById(deal.getStrategyId());

        return createNewOrder(deal, strategy, alert, alertTime, ticker);
    }

    private boolean needToOpenOrder(Deal deal, Alert alert) {
        boolean orderIsPresent = orderIsPresent(deal, alert.getName());
        boolean bestOrderIsPresent = bestOrderIsPresent(deal, alert.getName());

        /* Will not create new order if it was opened with the same alert
         * or if already present order with the higher alert number   */
        return !orderIsPresent && !bestOrderIsPresent;
    }

    private Order createNewOrder(Deal deal,
                                 Strategy strategy,
                                 Alert alert,
                                 String alertTime,
                                 String ticker) {
        return Order.builder()
                .name(alert.getName())
                .strategy(strategy.getName())
                .secret(alert.getSecret())
                .side(alert.getSide())
                .symbol(ticker)
                .userId(deal.getUserId())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .dealId(deal.getId())
                .build();
    }

    private boolean orderIsPresent(Deal deal, String alert) {
        int alertNumber = AlertMessage.valueByName(alert).getNumber();

        return switch (alertNumber) {
            case 1 -> deal.getFirstPrice() > 0;
            case 2 -> deal.getSecondPrice() > 0;
            case 3 -> deal.getThirdPrice() > 0;
            case 4 -> deal.getFourthPrice() > 0;
            case 5 -> deal.getFifthPrice() > 0;
            default -> true;
        };
    }

    private boolean bestOrderIsPresent(Deal deal, String alert) {
        int alertNumber = AlertMessage.valueByName(alert).getNumber();

        return switch (alertNumber) {
            case 1 -> (deal.getSecondPrice() + deal.getThirdPrice() + deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 2 -> (deal.getThirdPrice() + deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 3 -> (deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 4 -> deal.getFifthPrice() > 0;
            default -> false;
        };
    }

    public Order sendClosingOrder(Deal deal, Alert alert) {
        String alertTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return placeOrder(deal, alert, alertTime);
    }

}
