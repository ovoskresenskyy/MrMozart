package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum AlertMessage {

    /* Short open */
    S1("1S", 1, true, false),
    S2("2S", 2, true, false),
    S3("3S", 3, true, false),
    S4("4S", 4, true, false),
    S5("5S", 5, true, false),

    /* Short take profit */
    STP1("STP1", 1, false, true),
    STP2("STP2", 2, false, true),
    STP3("STP3", 3, false, true),
    STP4("STP4", 4, false, true),
    STP5("STP5", 5, false, true),

    /* Long open */
    L1("1L", 1, true, false),
    L2("2L", 2, true, false),
    L3("3L", 3, true, false),
    L4("4L", 4, true, false),
    L5("5L", 5, true, false),

    /* Short take profit */
    LTP1("LTP1", 1, false, true),
    LTP2("LTP2", 2, false, true),
    LTP3("LTP3", 3, false, true),
    LTP4("LTP4", 4, false, true),
    LTP5("LTP5", 5, false, true),

    /* Stop lose */
    SSL("SSL", 0, false, false),
    LSL("LSL", 0, false, false),

    /* Stop trend */
    STOP_TREND("Stop Trend", 0, false, false),
    STL("STL", 0, false, false),
    STS("STS", 0, false, false);

    private static final Map<String, AlertMessage> BY_NAME = new HashMap<>();

    static {
        for (AlertMessage e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    private final String name;
    private final int number;
    private final boolean tradeEntry;
    private final boolean takeProfit;

    AlertMessage(String name, int number, boolean tradeEntry, boolean takeProfit) {
        this.name = name;
        this.number = number;
        this.tradeEntry = tradeEntry;
        this.takeProfit = takeProfit;
    }

    /**
     * This method simply returns the value of enum by the received name
     *
     * @param name - Name of the alert received in the message
     * @return The enum if the matched side
     */
    public static AlertMessage valueByName(String name) {
        return BY_NAME.get(name);
    }

    /**
     * This method determines is the received message told to stop lossing the value
     *
     * @param message - Received message in the alert
     * @return True if it's StopLoss alert, false if not
     */
    public static boolean isStopLoss(String message) {
        return valueByName(message) == SSL || valueByName(message) == LSL;
    }

    /**
     * This method determines is the received message told to stop the trend
     *
     * @param message - Received message in the alert
     * @return True if it's StopTrend alert, false if not
     */
    public static boolean isStopTrend(String message) {
        return valueByName(message) == STOP_TREND
                || valueByName(message) == STS
                || valueByName(message) == STL;
    }

    /**
     * This method determines is the received message told to stop the trend
     *
     * @param message - Received message in the alert
     * @return True if it's StopTrend alert, false if not
     */
    public static boolean isStopTrendText(String message) {
        return valueByName(message) == STOP_TREND;
    }

    /**
     * This method determines is we need to immediately close the deal
     *
     * @param message - Message of the alert
     * @return True if deal must be closed, false if not
     */
    public static boolean isDealClosing(String message) {
        return isStopLoss(message) || isStopTrend(message);
    }

    /**
     * This method determines is the received order it's the trade entry
     *
     * @param message - Message of the alert
     * @return True if S1-5 / L1-5, false if not
     */
    public static boolean isTradeEntry(String message) {
        return valueByName(message).tradeEntry;
    }

    /**
     * This method determines is the received order it's the take profit
     *
     * @param message - Message of the alert
     * @return True if TP1-5, false if not
     */
    public static boolean isTakeProfit(String message) {
        return valueByName(message).takeProfit;
    }

    /**
     * Simple getter
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Simple getter
     *
     * @return number
     */
    public int getNumber() {
        return number;
    }
}
