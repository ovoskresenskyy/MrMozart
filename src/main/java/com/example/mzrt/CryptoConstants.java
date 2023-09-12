package com.example.mzrt;

public interface CryptoConstants {

    /**
     * ID of once deal for that strategy
     */
    int MOZART_DEAL_ID = 2;

    int MOZART_STRATEGY_ID = 1;
    int BF_STRATEGY_ID = 2;

    /**
     * This constant will hold the accuracy of the rounding for the deal prices
     */
    int ROUNDING_ACCURACY = 4;

    /**
     * This constant is holding the time of pause before order sending in ms
     */
    long ORDER_SENDING_PAUSE_TIME = 1000;
    /**
     * This constant is holding the time of pause between each iteration of determining
     * of profit price to send the Take profit order
     */
    long PROFIT_PRICE_SEARCHING_PAUSE_TIME = 1000 / 10;
}
