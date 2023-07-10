package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum AlertMessage {

    /* Short open */
    S1("1S", 1),
    S2("2S", 2),
    S3("3S", 3),
    S4("4S", 4),
    S5("5S", 5),

    /* Short take profit */
    STP1("STP1", 1),
    STP2("STP2", 2),
    STP3("STP3", 3),
    STP4("STP4", 4),
    STP5("STP5", 5),

    /* Long open */
    L1("1L", 1),
    L2("2L", 2),
    L3("3L", 3),
    L4("4L", 4),
    L5("5L", 5),

    /* Short take profit */
    LTP1("LTP1", 1),
    LTP2("LTP2", 2),
    LTP3("LTP3", 3),
    LTP4("LTP4", 4),
    LTP5("LTP5", 5),

    /* Stop lose */
    SSL("SSL", 0),
    LSL("LSL", 0),

    /* Stop trend */
    STOP_TREND("Stop Trend", 0),
    STL("STL", 0),
    STS("STS", 0);

    private static final Map<String, AlertMessage> BY_NAME = new HashMap<>();

    static {
        for (AlertMessage e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    private final String name;
    private final int number;

    AlertMessage(String name, int number) {
        this.name = name;
        this.number = number;
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
