package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum AlertMessage {

    STOP_LINE_CHANGE("Stop Line Change", 0),
    STOP_TREND("Stop Trend", 0),
    S1("1S", 1),
    S2("2S", 2),
    S3("3S", 3),
    S4("4S", 4),
    S5("5S", 5),
    L1("1L", 1),
    L2("2L", 2),
    L3("3L", 3),
    L4("4L", 4),
    L5("5L", 5);

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
        return valueByName(message) == STOP_LINE_CHANGE;
    }

    /**
     * This method determines is the received message told to stop the trend
     *
     * @param message - Received message in the alert
     * @return True if it's StopTrend alert, false if not
     */
    public static boolean isStopTrend(String message) {
        return valueByName(message) == STOP_TREND;
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
