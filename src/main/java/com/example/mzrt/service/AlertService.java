package com.example.mzrt.service;

import com.example.mzrt.model.Alert;
import com.example.mzrt.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    @Autowired
    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> findAll(){
        return alertRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Alert findById(int id) {
        return alertRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public Alert findByName(String name) {
        return alertRepository.findByName(name).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    public void deleteById(int id) {
        alertRepository.deleteById(id);
    }
}
