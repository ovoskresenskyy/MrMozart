package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.mzrt.enums.Side.getSideByMessage;
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

    /**
     * This method is responsible for separating the alert from the received text message
     *
     * @param message - Text message from the alert
     * @return Text of the alert if found (like 1S or 1L), "" if not
     */
    public String getAlertFromMessage(String message) {
        return getAlertNumberFromMessage(message)
                + getSideByMessage(message).shortName;
    }

    /**
     * This method is responsible for finding the alert number inside the text
     * <p>
     * Text example: "Price crossed above Fib1 level in long trend"
     *
     * @param message - Text message from the alert
     * @return The number of the alert if found, "" if not
     */
    private String getAlertNumberFromMessage(String message) {
        int index = message.indexOf("Fib");
        if (index == 0) {
            return "";
        }

        return String.valueOf(message.charAt(index + 3));
    }
}
