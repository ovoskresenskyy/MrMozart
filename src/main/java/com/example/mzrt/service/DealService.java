package com.example.mzrt.service;

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

    @Autowired
    public DealService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
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

    public Optional<Deal> getOpenedDealByTicker(int userId, String strategy, String ticker) {
        return dealRepository.getByUserIdAndStrategyAndTickerAndOpenTrue(userId, strategy.toLowerCase(), ticker);
    }

    /**
     * This method is responsible for closing the deal and updating all needed fields
     *
     * @param deal         - The deal to be closed
     * @param alert        - The closing alert
     */
    public void closeDeal(Deal deal, String alert) {
        BinanceDataHolder dataHolder = BinanceDataHolder.getInstance();
        double currentPrice = dataHolder.getFuturesByTicker(deal.getTicker()).getPrice();

        deal.setOpen(false);
        deal.setClosingPrice(currentPrice);
        deal.setClosingAlert(alert);
        deal.setLastChangeTime(LocalDateTime.now());
        save(deal);

        dataHolder.stopProfitTracker(deal.getId());
    }

    /**
     * This method is responsible for getting the list of the deals from the repository
     * according to the received parameters
     *
     * @param userId     - ID of the user which deals are
     * @param strategyId - ID of the chosen strategy
     * @param isOpen     - Mark if we need list of opened or closed deals
     * @return List of the deals
     */
    public List<Deal> getDeals(int userId, int strategyId, boolean isOpen) {
        return isOpen
                ? getOpenedDeals(userId, strategyId)
                : getClosedDeals(userId, strategyId);
    }

    /**
     * This method is responsible for getting the list of the all opened deals at the current time
     *
     * @param userId     - ID of the user which deals are
     * @param strategyId - ID of the chosen strategy
     * @return List of the opened deals
     */
    private List<Deal> getOpenedDeals(int userId, int strategyId) {
        return dealRepository.getByUserIdAndStrategyIdAndOpenIsTrue(userId,
                strategyId,
                Sort.by(Sort.Direction.DESC, "lastChangeTime"));
    }

    /**
     * This method is responsible for getting the list of the all closed deals
     *
     * @param userId     - ID of the user which deals are
     * @param strategyId - ID of the chosen strategy
     * @return List of the closed deals
     */
    private List<Deal> getClosedDeals(int userId, int strategyId) {
        return dealRepository.getByUserIdAndStrategyIdAndOpenIsFalse(userId,
                strategyId,
                Sort.by(Sort.Direction.DESC, "lastChangeTime"));
    }

    /**
     * This method is responsible for getting exist opened deal or creating the new one if it isn't.
     *
     * @param userId   - ID of the user
     * @param strategy - Strategy
     * @param ticker   - Coin pair like BTCUSDT
     * @param side     - Side name (Short / Long)
     * @return Exist deal or a new one
     */
    public Deal getDealByTicker(int userId, Strategy strategy, String ticker, String side) {
        Optional<Deal> openedDeal = getOpenedDealByTicker(userId, strategy.getName(), ticker);
        return openedDeal.orElseGet(() -> getNewDeal(userId, strategy, ticker, side));
    }

    /**
     * This method creates the new deal and save it in the repository
     * - Strategy name saving in lower case format
     * - Side name wil get from the enum to hold 'short' instead of 'sell' etc
     *
     * @param userId   - ID of user which is that deal
     * @param strategy - Name of the strategy
     * @param ticker   - Name of the pair like 'BTCUSDT'
     * @param side     - Sell / buy (because it's receives from the alert)
     * @return - The created deal, saved into the repository
     */
    public Deal getNewDeal(int userId,
                           Strategy strategy,
                           String ticker,
                           String side) {

        Deal newDeal = Deal.builder()
                .userId(userId)
                .strategy(strategy.getName().toLowerCase())
                .strategyId(strategy.getId())
                .ticker(ticker)
                .side(sideByName(side).name)
                .open(true)
                .build();

        return dealRepository.save(newDeal);
    }
}
