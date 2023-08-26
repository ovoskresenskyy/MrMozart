package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum AlertMessage {

    /* Short open */
    S1("1S", 1, true, false, false),
    S2("2S", 2, true, false,  false),
    S3("3S", 3, true, false, false),
    S4("4S", 4,true, true, false),
    S5("5S", 5, true, true, false),

    /* Short take profit */
    STP1("STP1", 1, false, true, true),
    STP2("STP2", 2, false, true, true),
    STP3("STP3", 3, false, true, true),
    STP4("STP4", 4, false, true, true),
    STP5("STP5", 5, false, true, true),

    /* Long open */
    L1("1L", 1, true, false, false),
    L2("2L", 2, true, false, false),
    L3("3L", 3, true, false, false),
    L4("4L", 4, true, true, false),
    L5("5L", 5, true, true, false),

    /* Short take profit */
    LTP1("LTP1", 1, false, true, true),
    LTP2("LTP2", 2, false, true, true),
    LTP3("LTP3", 3, false, true, true),
    LTP4("LTP4", 4, false, true, true),
    LTP5("LTP5", 5, false, true, true),

    /* Stop lose */
    SSL("SSL", 0, false, true, false),
    LSL("LSL", 0, false, true, false),

    /* Stop trend */
    STOP_TREND("Stop Trend", 0, false, true, false),
    STL("STL", 0, false, true, false),
    STS("STS", 0, false, true, false);

    private static final Map<String, AlertMessage> BY_NAME = new HashMap<>();

    static {
        for (AlertMessage e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    private final String name;
    private final int number;
    private final boolean isEntry;
    private final boolean isForbiddenToOpenNewDeals;
    private final boolean isTakeProfit;

    AlertMessage(String name,
                 int number,
                 boolean isEntry,
                 boolean isForbiddenToOpenNewDeals,
                 boolean isTakeProfit) {
        this.name = name;
        this.number = number;
        this.isEntry = isEntry;
        this.isForbiddenToOpenNewDeals = isForbiddenToOpenNewDeals;
        this.isTakeProfit = isTakeProfit;
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
        return valueByName(message) == SSL
                || valueByName(message) == LSL
                || valueByName(message) == STS
                || valueByName(message) == STL
                || valueByName(message) == STOP_TREND;
    }

    /**
     * This method determines is the received order is
     * used for opening the new deals is it's still closed
     *
     * @return True if S / l 1-3, false if not
     */
    public boolean isForbiddenToOpenNewDeals() {
        return isForbiddenToOpenNewDeals;
    }

    /**
     * This method determines is the received order it's the take profit
     *
     * @param message - Message of the alert
     * @return True if TP1-5, false if not
     */
    public static boolean isTakeProfit(String message) {
        return valueByName(message).isTakeProfit;
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

    /**
     * Simple getter
     *
     * @return isEntry
     */
    public boolean isEntry(){
        return isEntry;
    }
}
