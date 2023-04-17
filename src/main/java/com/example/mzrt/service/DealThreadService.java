package com.example.mzrt.service;

import com.example.mzrt.enums.Strategy;
import com.example.mzrt.model.Deal;
import lombok.Data;

@Data
public class DealThreadService implements Runnable {

    private DealService dealService;
    private int userId;
    private String ticker;

    public DealThreadService(DealService dealService, int userId, String ticker) {
        this.dealService = dealService;
        this.userId = userId;
        this.ticker = ticker + "USDT";
    }

    public void closeDeal(){
        run();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Deal openedDealByTicker = dealService.getOpenedDealByTicker(userId, Strategy.BLACK_FLAG.name.toLowerCase(), ticker);
        openedDealByTicker.setOpen(false);
        dealService.save(openedDealByTicker);
    }
}
