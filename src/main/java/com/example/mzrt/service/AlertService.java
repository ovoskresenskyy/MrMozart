package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;
import com.example.mzrt.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.mzrt.CryptoConstants.BF_STRATEGY_ID;
import static com.example.mzrt.enums.AlertMessage.isStopTrendText;
import static com.example.mzrt.enums.Side.getStopTrendAlert;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    @Autowired
    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> findByUserAndStrategy(int userId, int strategyId) {
        return alertRepository.findByUserIdAndStrategyId(userId,
                strategyId,
                Sort.by(Sort.Direction.ASC, "id"));
    }

    public Alert findById(int id) {
        return alertRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public Alert findByUserIdAndStrategyIdAndName(int userId, int strategyId, String name) {
        return alertRepository.findByUserIdAndStrategyIdAndName(userId,
                strategyId,
                name);
    }

    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    public void deleteById(int id) {
        alertRepository.deleteById(id);
    }

    public Alert getAlert(Deal deal, String message) {
        if (isStopTrendText(message)) {
            message = getStopTrendAlert(deal.getSide());
        }
        return findByUserIdAndStrategyIdAndName(deal.getUserId(), BF_STRATEGY_ID, message);
    }

}
