package com.example.mzrt.service;

import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.holder.PriceTrackers;
import com.example.mzrt.model.Deal;
import com.example.mzrt.model.Strategy;
import com.example.mzrt.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.mzrt.enums.Side.sideByName;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class DealService {

    private final DealRepository dealRepository;
    private final DealPriceService dealPriceService;
    private final PriceTrackers priceTrackers;

    @Autowired
    public DealService(DealRepository dealRepository,
                       DealPriceService dealPriceService,
                       PriceTrackers priceTrackers) {
        this.dealRepository = dealRepository;
        this.dealPriceService = dealPriceService;
        this.priceTrackers = priceTrackers;
    }

    public Deal findById(int id) {
        return dealRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public List<Deal> findAllOpened() {
        return dealRepository.findByOpenTrue();
    }

    public Deal save(Deal deal) {
        return dealRepository.save(deal);
    }

    public Optional<Deal> getOpenedDealByTicker(String strategy, String ticker) {
        return dealRepository.getByStrategyAndTickerAndOpenTrue(strategy.toLowerCase(), ticker);
    }

    /**
     * This method is responsible for closing the deal and updating all needed fields
     *
     * @param deal  - The deal to be closed
     * @param alert - The closing alert
     */
    public void closeDeal(Deal deal, String alert) {
        double currentPrice = priceTrackers.getFuturesTracker(deal.getTicker()).getPrice();

        deal.setOpen(false);
        deal.setClosingPrice(currentPrice);
        deal.setClosingAlert(alert);
        updateLastChangingTime(deal);
        save(deal);
    }

    /**
     * This method is responsible for getting the list of the deals from the repository
     * according to the received parameters
     *
     * @param strategyId - ID of the chosen strategy
     * @param isOpen     - Mark if we need list of opened or closed deals
     * @return List of the deals
     */
    public List<Deal> getDeals(int strategyId, boolean isOpen) {
        return isOpen
                ? getOpenedDeals(strategyId)
                : getClosedDeals(strategyId);
    }

    /**
     * This method is responsible for getting the list of the all opened deals at the current time
     *
     * @param strategyId - ID of the chosen strategy
     * @return List of the opened deals
     */
    private List<Deal> getOpenedDeals(int strategyId) {
        return dealRepository.getByStrategyIdAndOpenIsTrue(strategyId,
                Sort.by(Sort.Direction.DESC, "lastChangeTime"));
    }

    /**
     * This method is responsible for getting the list of the all closed deals
     *
     * @param strategyId - ID of the chosen strategy
     * @return List of the closed deals
     */
    private List<Deal> getClosedDeals(int strategyId) {
        return dealRepository.getByStrategyIdAndOpenIsFalse(strategyId,
                Sort.by(Sort.Direction.DESC, "lastChangeTime"));
    }

    /**
     * This method is responsible for getting exist opened deal or creating the new one if it isn't.
     *
     * @param strategy - Strategy
     * @param ticker   - Coin pair like BTCUSDT
     * @param side     - Side name (Short / Long)
     * @return Exist deal or a new one
     */
    public Deal getDealByTicker(Strategy strategy, String ticker, String side) {
        Optional<Deal> openedDeal = getOpenedDealByTicker(strategy.getName(), ticker);
        return openedDeal.orElseGet(() -> getNewDeal(strategy, ticker, side));
    }

    /**
     * This method creates the new deal and save it in the repository
     * - Strategy name saving in lower case format
     * - Side name wil get from the enum to hold 'short' instead of 'sell' etc
     *
     * @param strategy - Name of the strategy
     * @param ticker   - Name of the pair like 'BTCUSDT'
     * @param side     - Sell / buy (because it's receives from the alert)
     * @return - The created deal, saved into the repository
     */
    public Deal getNewDeal(Strategy strategy,
                           String ticker,
                           String side) {

        Deal newDeal = Deal.builder()
                .strategy(strategy.getName().toLowerCase())
                .strategyId(strategy.getId())
                .ticker(ticker)
                .side(sideByName(side).name)
                .open(true)
                .build();

        return dealRepository.save(newDeal);
    }

    /**
     * This method is responsible for updating prices inside the deal
     * according to received alert
     * <p>
     * It can update Entry or TP price
     * After that it's update average price and price profit
     *
     * @param deal  - Deal within which will update prices
     * @param alert - Received alert
     */
    public void updatePricesByAlert(Deal deal, AlertMessage alert) {
        dealPriceService.updatePrices(deal, alert);
        updateLastChangingTime(deal);
        save(deal);
    }

    /**
     * This method is simply updating last changing time, by getting the current one
     *
     * @param deal - The deal to be updated
     */
    public void updateLastChangingTime(Deal deal) {
        deal.setLastChangeTime(LocalDateTime.now());
    }

    /**
     * This method is responsible for updating profit prices
     * into the opened deal according to the ticker
     * <p>
     * It will be used after changing percent of profit for the ticker
     *
     * @param strategy - Strategy within ticker is used
     * @param ticker   - Ticker within which percent is changed
     */
    public void updateProfitPriceAtOpenedDeal(String strategy, String ticker) {
        Optional<Deal> openedDeal = getOpenedDealByTicker(strategy, ticker);
        if (openedDeal.isPresent()) {
            Deal deal = openedDeal.get();
            double profitPrice = dealPriceService.getProfitPrice(deal);
            deal.setProfitPrice(profitPrice);
            save(deal);
        }
    }
}
