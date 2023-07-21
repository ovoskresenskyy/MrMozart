package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum AlertMessage {

    /* Short open */
    S1("1S", 1,  false),
    S2("2S", 2,  false),
    S3("3S", 3,  false),
    S4("4S", 4,  false),
    S5("5S", 5,  false),

    /* Short take profit */
    STP1("STP1", 1,  true),
    STP2("STP2", 2,  true),
    STP3("STP3", 3,  true),
    STP4("STP4", 4,  true),
    STP5("STP5", 5,  true),

    /* Long open */
    L1("1L", 1,  false),
    L2("2L", 2,  false),
    L3("3L", 3,  false),
    L4("4L", 4,  false),
    L5("5L", 5,  false),

    /* Short take profit */
    LTP1("LTP1", 1,  true),
    LTP2("LTP2", 2,  true),
    LTP3("LTP3", 3,  true),
    LTP4("LTP4", 4,  true),
    LTP5("LTP5", 5,  true),

    /* Stop lose */
    SSL("SSL", 0,  false),
    LSL("LSL", 0,  false),

    /* Stop trend */
    STOP_TREND("Stop Trend", 0,  false),
    STL("STL", 0,  false),
    STS("STS", 0,  false);

    private static final Map<String, AlertMessage> BY_NAME = new HashMap<>();

    static {
        for (AlertMessage e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    private final String name;
    private final int number;
    private final boolean takeProfit;

    AlertMessage(String name, int number, boolean takeProfit) {
        this.name = name;
        this.number = number;
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
