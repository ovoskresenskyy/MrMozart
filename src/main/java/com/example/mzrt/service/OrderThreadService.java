package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static com.example.mzrt.CryptoConstants.PAUSE_TIME;

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
        pause(alert.getPause() * PAUSE_TIME);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForObject(alert.getWebhook(),
                new HttpEntity<>(order, headers),
                Order.class);
    }

    /**
     * This method makes the pause before order sending.
     * We use that to make sure that the order is opened before
     *
     * @param pauseTime - Time in ms
     */
    private void pause(long pauseTime) {
        try {
            Thread.sleep(pauseTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
